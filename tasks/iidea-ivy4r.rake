require 'buildr/ide/idea'
require 'buildr/ivy_extension'

# Ensures that the ivy deps are configured in before generating IDEA
# artifacts with iidea.

module Ivy4rAndIidea
  include Buildr::Extension

  after_define do |project|
    project.task('idea:generate').prerequisites.each do |iml|
      iml.enhance [ project.task('compiledeps'), project.task('testdeps') ]
    end
  end
end

class Buildr::Project
  include Ivy4rAndIidea
end

module Buildr
  module IntellijIdea
    class IdeaModule
      protected

      alias :test_dependency_details_without_ivy :test_dependency_details

      # Monkey-patched to find ivy sources
      def test_dependency_details
        test_dependency_details_without_ivy.collect do |dependency_path, export, source_path|
          if !source_path && dependency_path =~ %r{ivy/home}
            possible_source_path = dependency_path.
              sub(%r{/jars/}, '/sources/').sub(%r{\.jar$}, '-sources.jar')
            if File.exist?(possible_source_path)
              source_path = possible_source_path
            end
          end
          [dependency_path, export, source_path]
        end
      end

      # Monkey-patched to detect interproject deps
      def generate_lib(xml, dependency_path, export, source_path, project_dependencies)
        project_for_dependency = Buildr.projects.find do |project|
          project.packages.detect do |pkg|
            File.basename(pkg.to_s) == File.basename(dependency_path.to_s)
          end
        end
        if project_for_dependency
          if project_for_dependency.iml? &&
            !project_dependencies.include?(project_for_dependency) &&
            project_for_dependency != self.buildr_project
            generate_project_dependency(xml, project_for_dependency.iml.name, export, !export)
          end
          project_dependencies << project_for_dependency
        else
          generate_module_lib(xml, url_for_path(dependency_path), export, (source_path ? url_for_path(source_path) : nil), !export)
        end
      end
    end

    # Monkey-patched to allow for no_iml in the middle of a project tree
    module ProjectExtension
      def iml
        if iml?
          unless @iml
            # TODO: make this properly recursive
            @iml = (self.parent && self.parent.iml?) ? self.parent.iml.clone : IdeaModule.new
            @iml.buildr_project = self
          end
          return @iml
        else
          raise "IML generation is disabled for #{self.name}"
        end
      end
    end
  end
end
