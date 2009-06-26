# krobot.rb

# -*- encoding: utf-8 -*-

require 'javaws/bot/crobot/behavior'
require 'javaws/bot/crobot/actions'
require 'javaws/bot/crobot/tasks'

class CRobot < Behavior
  include JAVAWS::BOT::ROBOT
  CONF_YAML = "crobot.yml"
  attr :username, true
  attr :password, true
  attr :browser,  true
  attr :config,   true
  attr :cachedir, true
  attr :cache,    true
  attr :account,  true
  def initialize(options = nil)
    options = {} if options.nil?
    @config = options[:config]
    if @config.nil?
      confyml = options[:confyml] || CONF_YAML
      config  = JAVAWS::Configuration.new
      config.load confyml
      @config = config
    end
    @username = @config.read('crobot.username')
    @password = @config.read('crobot.password')
    @cachedir = @config.read('crobot.cachedir')
    @conds    = {:login? => false}
    @cache    = init({})
    @account  = init({}, 'account')
  end
  def save(data = @cache, fname = 'cache', method = 'w')
    home = ENV['HOME']
    dir  = @cachedir.sub(/\$\{HOME\}/, home)
    file = dir + "/" + fname
    @log.p "# save #{file}"
    Dir.mkdir(dir) if !File.exist?(dir)
    fout = open(file, method)
    fout.write(data.to_yaml)
  end
  def init(data = @cache, fname = 'cache', method = 'r')
    data = {} if data.nil?
    home = ENV['HOME']
    dir  = @cachedir.sub(/\$\{HOME\}/, home)
    file = dir + "/" + fname
    return data if !File.exist?(file)
    data = YAML::load(File.open(file))
    return purge(data)
    # return data
  end
  def purge(data = @cache, types = [String])
    data = {} if data.nil?
    data.keys.each do |key|
      data.delete(key) if types.index(key.class)
    end
    return data
  end
  def desc
    return @config.read('crobot.describe') || super
  end
end
