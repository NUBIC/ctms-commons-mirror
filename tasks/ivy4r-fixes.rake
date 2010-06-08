# Monkey patches to fix issues with ivy4r.

require 'buildr/ivy_extension'

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
