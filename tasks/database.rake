module DatabaseSetup
  DATABASE_PROPERTY_DEFAULTS = {
    "postgresql" => {
      "driver" => "org.postgresql.Driver",
      "dialect" => "org.hibernate.dialect.PostgreSQLDialect"
    },
    "oracle" => {
      "driver" => "oracle.jdbc.driver.OracleDriver",
      "dialect" => "org.hibernate.dialect.Oracle10gDialect"
    }
  }

  DEFAULT_CSM_PROPERTIES_FILENAME = "csm-test-connection.properties"

  def csm_db_properties_filename(root_project)
    basename = ENV['CSM_DB'] || DEFAULT_CSM_PROPERTIES_FILENAME
    basename << '.properties' unless basename =~ /.properties$/
    full_path = basename =~ %r{^/} ? basename : root_project.path_to("build", basename)
    unless File.exist?(full_path)
      sample_path = root_project._("build", DEFAULT_CSM_PROPERTIES_FILENAME)
      fail "Please create #{full_path}. There's a sample in #{sample_path}.sample."
    end
    full_path
  end

  def csm_db_properties(root_project)
    @csm_db_properties ||=
      begin
        filename = csm_db_properties_filename(root_project)
        raw_props = Hash.from_java_properties(File.read(filename))
        trace "Raw CSM test DB properties from #{filename} are #{raw_props.inspect}"
        url = raw_props["url"] or raise "\"url\" is required in #{filename}"
        db_type = (raw_props["db_type"] ||
          case url
          when /postgresql/i
            "postgresql"
          when /oracle/i
            "oracle"
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
