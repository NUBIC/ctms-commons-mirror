# Supports using ivy4r in a multimodule project

require 'fileutils'

# Ivy's home.dir must be absolute, so this is necessary
IVY_HOME = File.expand_path("../../ivy/home", __FILE__)
Buildr.settings.build['ivy']['home.dir'] = IVY_HOME

module ProjectIvyRepo
  include Buildr::Extension

  PROJECT_REPO_ROOT = File.join(IVY_HOME, "project-repo")

  # these actions are deliberately performed at buildfile load time
  FileUtils.mkdir_p PROJECT_REPO_ROOT
  File.open(File.expand_path("../../ivy/home/build-generated.properties", __FILE__), 'w') do |f|
    f.puts "project-repo.base=#{PROJECT_REPO_ROOT}"
  end

  # A list of sibling projects that this project depends on (in any
  # scope).  They need to be listed in the ivy.xml, too.  The names
  # will be resolved into projects relative to this project.
  def interproject_dependencies
    @interproject_dependencies ||= []
  end

  after_define do |proj|
    jar = proj.packages.detect { |p| p.to_s =~ /jar$/ }
    if jar && proj.ivy.own_file?
      proj.ivy.publish_options(
        :resolver => 'this-project',
        :artifactspattern => proj.path_to(:target, "[artifact]-[revision].[ext]"),
        :srcivypattern => proj.path_to(:target, "[artifact].[ext]"),
        :overwrite => true)

      jar.enhance do |task|
        # I want to be last for real
        task.enhance do
          # Publish the packaged artifact to the project-level repo so
          # it's resolvable by other projects.  For some reason
          # invoking the publish task doesn't work, but directly
          # invoking this method does work.
          proj.ivy.__publish__
        end
      end
    end

    unless proj.interproject_dependencies.empty?
      # Ensure that all sibling projects are built and published
      # before resolving this one.
      proj.task("ivy:resolve" =>
        proj.interproject_dependencies.collect { |n| proj.project(n).packages })
    end
  end
end

class Buildr::Project
  include ProjectIvyRepo
end

task("clean").enhance do
  rm_r ProjectIvyRepo::PROJECT_REPO_ROOT
end
