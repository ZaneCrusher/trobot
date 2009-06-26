# config.rb

# -*- encoding: utf-8 -*-

require 'yaml'

module JAVAWS

  class Configuration

    CONF_PATH = File.dirname(__FILE__) + "/../../etc/"
    attr :yaml, true

    def load(file = nil)
      return {} if file.nil?
      file = "#{CONF_PATH}#{file}" if !File.exist?(file)
      return {} if !File.exist?(file)
      @yaml = YAML::load(File.open(file))
      return @yaml
    end

    def save(file = nil)
      return if file.nil?
    end

    def read(name = nil, conf = nil)
      conf = @yaml if conf.nil?
      val  = conf
      name.split(/\./).each do |key|
        break if val.nil? || key.nil?
        next  if '' == key
        val = val[key]
      end
      return val
    end

  end

end
