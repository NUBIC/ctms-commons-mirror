require 'buildr_iidea'

# Subshells to create IDEA files.  This is necessary due to an
# incomprehensibly-introduced circular dependency issue in some
# ivy-configured modules that depend on other ivy-configured modules.

desc "Subshells to generate IDEA files"
task 'iidea' do
  fns = projects.collect { |p|
    [
      (p.iml.filename if p.iml?),
      (p.ipr.filename if p.ipr?)
    ].compact
  }.flatten.uniq.reject { |f| File.exist?(f) }

  fns.each do |filename|
    info "Subshelling to create #{filename}"
    cmd = "buildr #{filename} test=no"
    if Buildr.application.options.trace
      trace "About to execute `#{cmd}`"
      system(cmd)
    else
      `#{cmd}`
    end
  end

  info "Created #{fns.size} IDEA artifact#{'s' unless fns.size == 1}."
  fns.each do |filename|
    info "- #{filename}"
  end
  if fns.empty?
    warn "No artifacts created -- you might want to do iidea:clean first."
  end
end
