# browser.rb

# -*- encoding: utf-8 -*-

require 'rubygems'
require 'hpricot'
require 'json'
require 'net/http'
require 'cgi'
require 'iconv'

module JAVAWS
  class Browser
    UA_FIREFOX = ''
    AC_CONTENT = 'text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8'
    AC_CHARSET = 'ISO-8859-1,utf-8;q=0.7,*;q=0.7'
    attr :log, true
    # site
    attr :site, true
    # cookie
    attr :cookie, true
    # user-agent
    attr :ua, true
    # config
    attr :config, true
    # header
    attr :header, true
    # html
    attr :html, true
    # resp
    attr :resp, true
    # history
    attr :history, true
    def initialize(options = nil)
      options = {} if options.nil?
      @config = options[:config]
      @log    = options[:log]
      @site   = options[:site]   || @config.read('browser.site')
      @cookie = options[:cookie] || @config.read('browser.cookie')
      @ua     = options[:ua]     || @config.read('browser.ua')
      @charset= options[:charset]|| @config.read('browser.charset')
      @ua     = UA_FIREFOX if @ua.nil?
      if !@log.respond_to?'p'
        # print simple log
        def @log.p(message)
          self << ("\n#{message}")
        end
      end
      @header    = {}
      @history   = []
      @iocharset = "gb18030"
      @lang      = ENV['LANG']
      @iocharset = @lang.split(/\./)[1].downcase if !@lang.nil? && @lang.match(/\./)
    end
    def is_debug?
      return @use_debug if !@use_debug.nil?
      @use_debug = false
      @use_debug = (true == @config.read('browser.debug'))
      return @use_debug
    end
    def is_session_created?
      return !@cookie.nil? && '' != @cookie.strip
    end
    def conv(str = nil)
      return str if @iocharset == @charset
      return Iconv.iconv(@iocharset, @charset, str)
    end
    def request(uri = nil, params = nil, options = nil)
      raise 'URI Empty' if uri.nil?
      params = {} if params.nil?
      url = uri
      url = "#{@site}#{uri}" if uri.match(/^https{0,1}\:\/\//).nil?
      @log.p "> open #{url}"
      http = URI.parse(url)
      conn = Net::HTTP.new(http.host, http.port)
      @header['referer'] = @history[0] || ''
      @header['referer'] = options[:referer] if !options.nil? && !options[:referer].nil?
      @header.delete('referer') if !options.nil? && options[:noreferer]
      # puts @header['referer']
      @header['cookie'] = @cookie || "" # if !@cookike.nil?
      # @header['cookie'] = @header['set-cookie']
      @header['user-agent'] = @ua
      @header['accept'] = AC_CONTENT
      @header['accept-charset'] = AC_CHARSET
      resp, data = nil, nil
      if (nil == params)
        resp, data = conn.get(url, @header)
      else
        str_params = ""
        params.each do |k, v|
          if v.class == Array
            v.each do |vv|
              if !vv.nil?
                str_params = "#{str_params}&#{URI.escape(k.to_s)}=#{URI.escape(vv.to_s)}"
              end 
            end
          else
            str_params = "#{str_params}&#{URI.escape(k.to_s)}=#{URI.escape(v.to_s)}"
          end
        end
        if(self.is_debug?)
          params.each do |k, v|
              if v.class == Array
                @log.p(conv("> post #{k}=#{v.join(",")}"))
              else 
                @log.p(conv("> post #{k}=#{v}"))
              end
          end
        end
        resp, data = conn.post(url, str_params, @header)
      end
      # debug header
      if(self.is_debug?)
        resp.each { |k, v|
            @log.p("< acpt #{k}=#{v}")
        }
      end
      if(!resp.response['set-cookie'].nil?)
        # @cookie = resp.response['set-cookie']
        @cookie = combine_cookie(resp.response['set-cookie'], @cookie)
        @log.p("< sess #{@cookie}")
      end
      @history.insert(0, url)
      @resp, @html = resp, data
      return resp, data
    end
    def location(resp = @resp)
      loct = nil
      loct = resp.response['location'] if !resp.nil? && !resp.response.nil?
      return loct
    end
    def forward?(resp = @resp)
      return resp.code_type == Net::HTTPMovedPermanently
    end
    def combine_cookie(ncookie = nil, ocookie = @cookie)
      ocookie = ncookie if ocookie.nil?
      cookie = "#{ocookie}"
      cookie = "#{ncookie}; #{ocookie}" if "#{ocookie}".index("#{ncookie}").nil?
      return cookie
      # TODO
      ohash = CGI::Cookie.parse("#{ocookie}")
      nhash = CGI::Cookie.parse("#{ncookie}")
      nhash.each do |k, v|
        ohash[k] = v
      end
      ohash.each do |k, v|
        # ohash.delete(k) if ['expires', 'path', 'domain'].index(k)
        ohash.delete(k) if ['_uid', 'SERVERID', '_user'].index(k).nil?
      end
      puts "aaaaaaaa => " + ohash.keys.join(";")
      return ohash.values.join("; ").gsub(/path\=\;*/, '')
    end
    def search(expr = nil, data = nil)
      # @log.p "# expr #{expr}"
      return nil if expr.nil?
      data = @html if data.nil?
      html = Hpricot.parse(data)
      elem = html.search(expr)
      return elem
    end
    def parse(data = @html)
      return JSON.parse(data)
    end
  end
end
