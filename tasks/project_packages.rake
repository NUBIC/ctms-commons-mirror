require 'buildr'

# Extends project with an accessor to find the java packages defined
# by that project and one to use to inform bnd that those are the only
# ones to export.

class Buildr::Project
  def java_packages
    @java_packages ||= Dir[ File.join( _(:source, :main, :java), "**/*.java" ) ].collect { |f|
      File.read(f).scan(/package\s+(\S+);/).flatten.first
    }.compact.uniq
  end

  def bnd_export_package
    @bnd_export_package ||= java_packages.collect { |p| "#{p};version=#{version}" }.join(',')
  end
end
