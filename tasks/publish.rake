# Tasks for publishing from the local project repo to CBIIT

namespace :publish do
  PROJECT_PUBLICATION_LOCAL_ROOT = File.expand_path("../../publish-repo", __FILE__)

  task :version_check do
    projects.each do |p|
      unless p.version =~ /^\d+\.\d+.\d+\.RELEASE$/
        fail "#{p} has a non-release version.\nPlease set it to x.y.z.RELEASE (according to the policy in the README) and commit before attempting to release."
      end
    end
  end

  def which_vcs
    @which_vcs ||= begin
                     p = project('ctms-commons')
                     if File.directory?(p._('.git'))
                       :git
                     elsif File.directory?(p._('svn'))
                       :svn
                     end
                   end
  end

  task :vcs_check do
    case which_vcs
    when :git
      unless `git status -s`.empty?
        fail "Outstanding changes in the working directory.  Please resolve them before releasing."
      end
    when :svn
      unless `svn status`.empty?
        fail "Outstanding changes in the working directory.  Please resolve them before releasing."
      end
    end
  end

  desc "Ensure that the project is ready to publish"
  task :check => [:version_check, :vcs_check] do
    info "Everything seems to be ready to publish."
  end

  desc "Tag a release version in the project subversion repo"
  task :tag => :check do
    unless which_vcs == :svn
      fail "Tagging may only be done from an svn checkout."
    end
    system("svn cp ^/trunk ^/tags/releases/#{CTMS_COMMONS_VERSION} -m 'Tag #{CTMS_COMMONS_VERSION}'")
    fail "Tagging failed" unless $? == 0
  end

  task :url do |t|
    class << t; attr_accessor :value; end
    t.value = "https://ncisvn.nci.nih.gov/svn/cbiit-ivy-repo/trunk/#{CTMS_COMMONS_IVY_ORG}"
  end

  task :repo => :url do |t|
    class << t; attr_accessor :path; end
    mkdir_p PROJECT_PUBLICATION_LOCAL_ROOT
    FileUtils.cd PROJECT_PUBLICATION_LOCAL_ROOT do
      if File.directory?(File.join(CTMS_COMMONS_IVY_ORG, '.svn'))
        info "Updating publish repo checkout at #{File.expand_path('.')}"
        system("svn update #{CTMS_COMMONS_IVY_ORG}")
        unless $? == 0
          fail "Update failed.  Please check the subversion output for clues."
        end
      else
        url = task("publish:url").value
        info "Checking out publish repo"
        info "  from #{url}"
        info "    to #{File.expand_path('.')}"
        system("svn checkout #{task("publish:url").value}")
        unless $? == 0
          fail "Checkout failed.  Please check the subversion output for clues."
        end
      end
    end
    t.path = File.join(PROJECT_PUBLICATION_LOCAL_ROOT, CTMS_COMMONS_IVY_ORG)
  end

  task :check_clean_repo => :repo do |t|
    repo = task("publish:repo").path
    statuses = repo_status.collect { |st, path| st }.uniq
    unless statuses.empty?
      fail "The local copy of the publish repo is dirty (#{statuses.inspect}).  Please clean it up before proceeding."
    end
  end

  # TODO: this is not working
  task :build => [task("clean"), task("package")]

  task :copy => [:check_clean_repo, :repo] do
    publish_repo = task("publish:repo").path
    project_repo = ProjectIvyRepo::PROJECT_REPO_ROOT
    prefix = File.join(project_repo, CTMS_COMMONS_IVY_ORG) + "/"
    artifacts = projects.collect { |p| p.version }.uniq.
      collect { |version| Dir[File.join(prefix, "*", version, "**/*")] }.flatten
    artifacts.each do |artifact|
      target = File.join(publish_repo, artifact.sub(prefix, ''))
      FileUtils.mkdir_p File.dirname(target)
      FileUtils.cp artifact, target
      system("svn add --parents #{target}")
    end
    info "Copied #{artifacts.size} artifacts to the local publish repo."
  end

  desc "Prepare the project artifacts for publication"
  task :prepare => [:check, :build, :copy] do
    info "The local checkout of the target repo now contains the artifacts for #{CTMS_COMMONS_VERSION}."
    info "Please verify they are correct, then run `buildr publish:commit`."
    info "(The local checkout is in #{task("publish:repo").path}.)"
  end

  desc "Commit the prepared artifacts"
  task :commit => :repo do
    all_statuses = repo_status.collect { |st, path| st }.uniq
    unless all_statuses == %w(A)
      fail "You can only publish adds, not changes: #{all_statuses.join(' ')}"
    end
    info "Committing #{repo_status.size} changes."
    system("svn commit #{task("publish:repo").path} -m 'Publishing #{CTMS_COMMONS_VERSION}'")
    info "If the commit succeeded, please run `buildr publish:tag`."
    info "Then update the version in the buildfile to the next development version and commit."
  end

  desc "Remove all pre-publish artifacts from the local copy of the publish repo"
  task :clean => :repo do
    repo = task("publish:repo").path
    system("svn revert --recursive #{repo}")
    repo_status.select { |st, path| st == '?' }.collect { |st, path| path }.
      each { |file| FileUtils.rm_rf file }
  end

  def repo_status
    `svn status #{task("publish:repo").path}`.split("\n").collect { |line| line.split(/\s+/, 2) }
  end
end
