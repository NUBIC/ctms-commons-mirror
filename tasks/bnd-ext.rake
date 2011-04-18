require 'buildr'

# Helper methods for building bundles using buildr-bnd.

module AutoconfigureBundles
  include Buildr::Extension

  after_define do |project|
    bundle = project.packages.detect { |pkg| Buildr::Bnd::BundleTask === pkg }
    if bundle
      bundle["Export-Package"] ||= project.bnd_export_package
      bundle["Import-Package"] ||= project.bnd_import_package
    end
  end
end


class Buildr::Project
  include AutoconfigureBundles

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
end
