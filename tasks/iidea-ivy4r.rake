require 'buildr_iidea'
require 'buildr/ivy_extension'

# Ensures that the ivy deps are configured in before generating IDEA
# artifacts with iidea.

module Ivy4rAndIidea
  include Buildr::Extension

  after_define do |project|
    project.task('iidea:generate').prerequisites.each do |iml|
      iml.enhance [ project.task('compiledeps'), project.task('testdeps') ]
    end
  end
end

class Buildr::Project
  include Ivy4rAndIidea
end

# Monkey-patches iidea to include jar deps which are not in the m2
# repo (i.e., locally-cached ivy deps).  Also detects things in the
# project repo as being intermodule deps.

module Buildr
  module IntellijIdea
    class IdeaModule < IdeaFile
      def module_root_component
        m2repo = Buildr::Repositories.instance.local

        create_component("NewModuleRootManager", "inherit-compiler-output" => "false") do |xml|
          generate_compile_output(xml)
          generate_content(xml)
          generate_initial_order_entries(xml)

          # Note: Use the test classpath since IDEA compiles both "main" and "test" classes using the same classpath
          self.test_dependencies.each do |dependency_path|
            export = self.main_dependencies.include?(dependency_path)
            project_for_dependency = Buildr.projects.detect do |project|
              project.packages.detect { |pkg| File.basename(pkg.to_s) == File.basename(dependency_path) }
            end
            if project_for_dependency
              if project_for_dependency.iml?
                generate_project_dependency( xml, project_for_dependency.iml.name, export )
              end
            elsif dependency_path.to_s.index(m2repo) == 0
              entry_path = dependency_path
              unless self.local_repository_env_override.nil?
                entry_path = entry_path.sub(m2repo, "$#{self.local_repository_env_override}$")
              end
              generate_module_lib(xml, "jar://#{entry_path}!/", export )
            elsif dependency_path.to_s =~ /jar$/
              generate_module_lib(xml, "jar://#{dependency_path}!/", export)
            end
          end

          self.resources.each do |resource|
            generate_module_lib(xml, "#{MODULE_DIR_URL}/#{relative(resource.to_s)}", true)
          end

          xml.orderEntryProperties
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
