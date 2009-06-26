# Rakefile

# -*- encoding: utf-8 -*-

require 'rake/testtask'
require 'rake/rdoctask'
require 'rake/gempackagetask'
require 'rake/contrib/sshpublisher'

spec = Gem::Specification.new do |s|
  s.name = 'krobot'
  s.version = '0.1.0'
  s.platform = Gem::Platform::RUBY
  s.summary = 'Kaixin Robot'
  s.description = s.summary
  s.files = FileList['{examples,lib,tasks,test}/**/*'] + %w(CHANGELOG.rdoc init.rb LICENSE Rakefile README.rdoc) - FileList['test/*.log']
  s.require_path = 'lib'
  s.has_rdoc = true
  s.test_files = Dir['test/**/*_test.rb']
  s.author = 'jhsea3do'
  s.email = 'jhsea3do@gmail.com'
  s.homepage = 'http://krobot.javaws.com/'
  s.rubyforge_project = 'krobot'
end

desc 'Default: run all tests.'
task :default => :test
 
desc "Test the #{spec.name} plugin."
Rake::TestTask.new(:test) do |t|
  t.libs << 'lib'
  t.test_files = spec.test_files
  t.verbose = true
end
 
begin
  require 'rcov/rcovtask'
  namespace :test do
    desc "Test the #{spec.name} plugin with Rcov."
    Rcov::RcovTask.new(:rcov) do |t|
      t.libs << 'lib'
      t.test_files = spec.test_files
      t.rcov_opts << '--exclude="^(?!lib/)"'
      t.verbose = true
    end
  end
rescue LoadError
end
 
desc "Generate documentation for the #{spec.name} plugin."
Rake::RDocTask.new(:rdoc) do |rdoc|
  rdoc.rdoc_dir = 'rdoc'
  rdoc.title = spec.name
  rdoc.template = '../rdoc_template.rb'
  rdoc.options << '--line-numbers' << '--inline-source'
  rdoc.rdoc_files.include('README.rdoc', 'CHANGELOG.rdoc', 'LICENSE', 'lib/**/*.rb')
end
 
desc 'Generate a gemspec file.'
task :gemspec do
  File.open("#{spec.name}.gemspec", 'w') do |f|
    f.write spec.to_ruby
  end
end
 
Rake::GemPackageTask.new(spec) do |p|
  p.gem_spec = spec
  p.need_tar = true
  p.need_zip = true
end
 
desc 'Publish the beta gem.'
task :pgem => [:package] do
  # Rake::SshDirPublisher
end
 
desc 'Publish the API documentation.'
task :pdoc => [:rdoc] do
  # Rake::SshDirPublisher
end
 
desc 'Publish the API docs and gem'
task :publish => [:pgem, :pdoc, :release]
 
desc 'Publish the release files to RubyForge.'
task :release => [:gem, :package] do
  require 'rubyforge'
  
  ruby_forge = RubyForge.new.configure
  ruby_forge.login
  
  %w(gem tgz zip).each do |ext|
    file = "pkg/#{spec.name}-#{spec.version}.#{ext}"
    puts "Releasing #{File.basename(file)}..."
    
    ruby_forge.add_release(spec.rubyforge_project, spec.name, spec.version, file)
  end
end

Dir['tasks/**/*.rake'].each {|rake| load rake}
