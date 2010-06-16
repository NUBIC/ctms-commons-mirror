require 'buildr'

# Helper methods for building bundles using buildr-bnd.

class Buildr::Project
  def java_packages
    @java_packages ||= Dir[ File.join( _(:source, :main, :java), "**/*.java" ) ].collect { |f|
      File.read(f).scan(/package\s+(\S+);/).flatten.first
    }.compact.uniq
  end

  def bnd_export_package
    @bnd_export_package ||= java_packages.collect { |p| "#{p};version=#{version}" }.join(',')
  end

  def bnd_import_package
    "gov.nih.nci.*, *;resolution:=optional"
  end

  def configure_bundle(bundle)
    bundle["Export-Package"] = bnd_export_package
    bundle["Import-Package"] = bnd_import_package
    bundle["Include-Resource"] = _(:target, :resources) if File.exist?(_(:target, :resources))
  end
end
