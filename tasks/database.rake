module DatabaseSetup
  DATABASE_PROPERTY_DEFAULTS = {
    "postgresql" => {
      "driver" => "org.postgresql.Driver",
      "dialect" => "org.hibernate.dialect.PostgreSQLDialect"
    }
  }

  DEFAULT_CSM_PROPERTIES_FILENAME = "csm-test-connection.properties"

  def csm_db_properties_filename
    basename = ENV['CSM_DB'] || DEFAULT_CSM_PROPERTIES_FILENAME
    basename << '.properties' unless basename =~ /.properties$/
    full_path = basename =~ %r{^/} ? basename : project('ctms-commons').path_to("build", basename)
    unless File.exist?(full_path)
      sample_path = project('ctms-commons')._("build", DEFAULT_CSM_PROPERTIES_FILENAME)
      fail "Please create #{full_path}. There's a sample in #{sample_path}.sample."
    end
    full_path
  end

  def csm_db_properties
    @csm_db_properties ||=
      begin
        filename = csm_db_properties_filename
        raw_props = Hash.from_java_properties(File.read(filename))
        trace "Raw CSM test DB properties from #{filename} are #{raw_props.inspect}"
        url = raw_props["url"] or raise "\"url\" is required in #{filename}"
        db_type = (raw_props["db_type"] ||
          case url
          when /postgresql/i
            "postgresql"
          else
            raise "Unable to guess database type from #{url}.  " <<
              "Please specify \"db_type\" in #{filename}."
          end).downcase

        Hash[ raw_props.merge("db_type" => db_type).tap do |props|
          DATABASE_PROPERTY_DEFAULTS.collect { |k, v| v.keys }.flatten.uniq.each do |key|
            props[key] ||= DATABASE_PROPERTY_DEFAULTS[db_type][key] or
              raise "No default #{key} for #{db_type.inspect}.  " <<
              "Please specify #{key.inspect} in #{filename}."
          end
        end.collect { |k, v| ["csm_db.#{k}", v] } ]
      end
  end
end

class Buildr::Project
  include DatabaseSetup
end
