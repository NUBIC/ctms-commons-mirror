# Monkey patches to fix issues with ivy4r.

require 'buildr/ivy_extension'

# http://github.com/klaas1979/ivy4r/issues/issue/4
class Buildr::Ivy::IvyConfig
  def post_resolve_task_list
    @post_resolve_task_list ||= []
  end
end

# http://github.com/klaas1979/ivy4r/issues/issue/6
class Buildr::Ivy::IvyConfig
  alias :base_file :file
  def file
    @project.path_to(base_file)
  end
end

# ivy4r assumes that the package tasks have `manifest` and `with`
# methods, which makes it incompatible with buildr-bnd.  Debatable
# whose problem this is, so I haven't reported it anywhere.
require 'buildr_bnd'
class Buildr::Bnd::BundleTask
  def manifest
    self.to_params
  end

  def with(*args)
  end
end
