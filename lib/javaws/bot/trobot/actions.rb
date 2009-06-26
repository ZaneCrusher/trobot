# actions.rb

module JAVAWS::BOT::ACTION

# reset action
class ResetAction < BaseAction
  def on
    @robot.cache = {}
    @robot.save(@robot.cache, 'cache')
    @robot.account = {}
    @robot.save(@robot.account, 'account')
    @browser.cookie = nil
    @browser.header = {}
  end
end

class HomeAction < BaseAction
  attr :html, true
  def village(elem = nil)
    sel = 'sel' == elem.attributes['class']
    a = elem.search("a")[0]
    href = a.attributes['href']
    id   = href.match(/newdid\=(\d+)/)[1]
    name = a.inner_html
    x = elem.search("td[@class='x']")[0].inner_html.match(/([\-]*\d+)/)[1]
    y = elem.search("td[@class='y']")[0].inner_html.match(/([\-]*\d+)/)[1]
    k = TRAVIAN::Karte.new
    k.x = x
    k.y = y
    v = TRAVIAN::Village.new
    v.id = id
    v.name = name
    v.k = k
    v.sel = sel
    return v
  end
  def me
    uid = @robot.spieler.uid
    return uid if !uid.nil?
    @browser.search("div#sleft/p/a").each do |elem|
      break if !uid.nil?
      href = elem.attributes['href']
      next if href.nil? || href.match(/uid\=/).nil?
      uid = href.match(/spieler.*uid\=(\d+)/)[1]
    end
    @robot.spieler.uid = uid if !uid.nil?
    return @robot.spieler.uid
  end
  def switch(did = nil)
    did = @options[:switch] if did.nil?
    did = @robot.spieler.next_did if did.nil? || did == 'next' || did == ''
    return if did.nil? || did.match(/\d+/).nil?
    uri = suri
    url = "#{uri}?newdid=#{did}"
    @browser.request(url)
  end
  def res
    # vlist if @robot.spieler.village.nil?
    div = @browser.search("div#res")[0]
    r   = @robot.spieler.village.res
    [4,3,2,1].each do |type|
      expr    = /(\d+)\/(\d+)/
      td      = div.search("td#l#{type}")[0]
      t       = [:lumber,:clay,:iron,:crop][4-type]
      r[t].i  = td.attributes['title'].to_i
      r[t].c  = td.inner_html.match(expr)[1].to_i
      r[t].m  = td.inner_html.match(expr)[2].to_i
    end
    return r
  end
  def tlist
    # troop
  end
  def current_v(v, h1 = nil)
    return if h1.nil?
    v.name = h1.inner_html
    begin
      divid = div.inner_html.match(/\<\/map\>\s*\n\s*\<div\ (.*)\>\s*\n\s*\<img/)[1]
      ktype = divid.match(/id=\"(\w+)\"/)[1]
      ktype = ktype.to_sym if !ktype.nil?
      v.ktype = ktype
    rescue
    end
  end
  def build(elem = nil)
    return nil if elem.nil?
    begin
      id    = elem.attributes['href'].match(/id\=(\d+)/)[1].to_i
      title = elem.attributes['title']
      expr  = /(.*)\ (\d+)$/
      mexpr = title.match(expr)
      name  = 'N/A'
      lv    = 0
      name  = mexpr[1].strip if !mexpr.nil?
      lv    = mexpr[2].strip.to_i if !mexpr.nil?
      build = TRAVIAN::Build.new
      build.id = id
      build.name = name
      build.lv = lv
    rescue
      build = nil
    end
    return build
  end
  def blist
    before
    v = nil
    begin
      div  = @browser.search("div#content")[0]
      h1   = div.search("h1")[0]
      v    = TRAVIAN::Village.new # @robot.spieler.village
      current_v(v, h1)
      begin
        div.search("div.village_map/map#rx/area") do |elem|
          b = build(elem) 
          v.builds[b.id] = b
        end
      rescue
      end
      begin
        div.search("map#map2/area") do |elem|
          b = build(elem) 
          v.builds[b.id] = b
        end
      rescue
      end
      begin
        div.search("map#map1/area") do |elem|
          b = build(elem) 
          v.builds[b.id] = b
        end
      rescue
      end
    rescue
      # ... not regular expr
    end
    return v
  end
  def vlist
    before
    begin
      div = @browser.search("div#vlist")[0]
      raise "not login" if div.nil?
      div.search("table[@class='vlist']/tbody/tr") do |elem|
        v = village(elem)
        next if v.nil?
        @robot.spieler.villages[v.id] = v
=begin
        ov = @robot.spieler.villages[v.id]
        @robot.spieler.villages[v.id] = v if ov.nil?
        next if ov.nil?
        ov.update v
=end
      end
      # @log.p @robot.spieler.village
    rescue
      @log.p "! vlst error: #{$!}"
      # @log.p @browser.html
    end
    return @robot.spieler.villages
  end
  def stime
    before
    ltime = 0
    tp1 = @browser.search("span#tp1")[0].inner_html.match(/\d+\:\d+\:\d+/)[0]
    @robot.timer(tp1)
    return tp1
  end
  def before
    data = nil
    data = @browser.html \
      if !@browser.html.nil? && !@browser.search("div#sright").nil?
  end
  def on
    super
    return if !@html.nil?
    uri = suri
    @browser.request(uri) if @browser.html.nil?
    vlist
    exec
  end
  def exec
    options = @options || {}
    params = options.map{|k,v| [k.to_s, v.to_s]}.flatten
    return if params.nil? || params.length < 1
    m = params[0].to_sym
    @log.p "# exec #{m} to #{self.class}"
    self.send m if self.respond_to? m
  end
  def suri
    return "/dorf1.php"
  end
end

# login action
class LoginAction < BaseAction
  attr :faction, true
  attr :fparams, true
  EXPR_COOKIE = /T3E\=.*/
  def validate
    return !@browser.cookie.nil? && @browser.cookie.match(EXPR_COOKIE)
  end
  def before
    if validate
      @robot.conds[@cond] = true
      return
    end
    # test cookie expires?
    @browser.request("/login.php")
    form = @browser.search("form[@name='snd']")[0]
    @faction = form.attributes['action']
    @fparams = {}
    form.search("input").each do |input|
      type  = input.attributes['type']
      name  = input.attributes['name']
      value = input.attributes['value']
      value = @robot.password if type == 'password'
      value = @robot.username if type == 'text'
      @fparams[name] = value
    end
  end
  def on
    # super
    return true if @robot.conds[@cond]
    uri = @faction
    uri = "/#{uri}" if uri.match(/^\//).nil?
    suc = false
    begin
      @browser.request(uri, @fparams)
      suc = validate
    rescue
      @log.p "! open error: #{$!}"
      suc = false
    end
    @robot.conds[@cond] = suc
  end
  def after
    @robot.cache[:cookie] = @browser.cookie
    @robot.save
  end
end

# logout action
class LogoutAction < BaseAction
  def on
    super
    uri = "/logout.php"
    begin
      @browser.request(uri)
      @robot.conds[:login?] = false
      @browser.cookie = nil
    rescue
      @robot.conds[:login?] = false
      @browser.cookie = nil
    end
  end
  def after
    @robot.cache[:cookie] = @browser.cookie
    @robot.save
  end
end

class Dorf2Action < HomeAction
  def blist
    super
  end
  def suri
    return "/dorf2.php"
  end
end

# home action
class Dorf1Action < HomeAction
  def checkplus
    @robot.conds[:plus?] = false
  end
  def resources
    res
    # production
  end
  def res
    super
  end 
  def production
    before
    r = @robot.spieler.village.res
    div = @browser.search("div#production")[0]
    div.search("table/tr") do |elem|
      tds = elem.search("td")
      next if tds.nil? || tds.length < 4
      type = tds[0].search("img")[0].attributes['class']
      incr = tds[2].search("b")[0].inner_html.to_s.match(/\d+/)[0]
      t = [:lumber, :clay, :iron, :crop][type.match(/\d/)[0].to_i - 1]
      r[t].i = incr.to_i
    end
    return r
  end
  def before
    super
  end
  def on
    return if !@html.nil?
    uri = suri
    @browser.request(uri) if @browser.html.nil?
    exec
    vlist
    resources
  end
  def after
  end
end

class ShowAction < HomeAction
  def show
    opt = {
      :all	 => nil,
      :spieler	 => nil,
      :village	 => nil,
      :villages	 => nil,
      :builds	 => nil,
      :resources => nil
    }
    opt.merge! @options
    u  = @robot.spieler
    vs = u.villages
    v  = u.village
    rs = res # v.resources
    bsv= blist # v.builds
    if opt[:all] || opt[:spieler] || opt[:u]
      @log.p "> show me.uid: #{me}"
    end
    if opt[:all] || opt[:vs] || opt[:villages]
      vs.each do |k, v|
        @log.p @browser.conv("> show #{v}")
      end
    end
    if opt[:all] || opt[:res] || opt[:resources]
      [:lumber, :clay, :iron, :crop].each do |t|
        @log.p "> show #{t}:\t#{rs[t]}"
      end
    end
    if opt[:all] || opt[:bs] || opt[:builds]
      @log.p @browser.conv("> show bsv: #{bsv.name}")
      bs = bsv.builds
      bs.keys.sort.each do |k|
        @log.p @browser.conv("> show #{bs[k]}")
      end
    end
    timer = @robot.timer
    timer.stime = self.stime
    t = timer.time2str(timer.now)
    @log.p "> time #{t}"
  end
  def on
    show
  end
end

end
