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
    @browser.header = nil
  end
end

# home action
class HomeAction < BaseAction
  def on
    super
    return
    uri = "/home/"
    url = uri
    params = nil
    # TODO params = @browser.location
    suc = false
    begin
      @browser.request(url, params)
      @browser.search("div.srl div.srl1 div.l120_s").each do |e|
      #  suc = true if "" != "#{e}"
      #  break if suc
      #  puts e
         title = e[:title]
         @robot.account[:uid] = "#{title.match(/ID\:\d*/)}".sub(/^ID\:/, '')
         @robot.account[:logo] = {
           :img => e.to_s.match("http.*\/logo\/.*\.jpg")[0],
           :height => 120,
           :width => 120
         }
      end
      suc = true
    rescue
      @log.p "! open error: #{$!}"
      suc = false
    end
  end
  def after
    @robot.account[:email] = @robot.username
    @robot.account[:plugins] = []
    uri = "/app/left.php"
    url = uri
    params = {}
    @browser.request(url, params)
    @browser.parse.each do |hash|
      next if hash.nil? || hash['link'].index(/!house/).nil?
      @robot.account[:plugins] << hash
      @log.p "# link #{hash['title']}\t#{hash['link']}"
    end
    @robot.save(@robot.account, 'account')
  end
end

# login action
class LoginAction < BaseAction
  def before
    if !@browser.cookie.nil? && @browser.cookie.length > 10
      @robot.conds[@cond] = true 
    end
    # test cookie expires?
  end
  def on
    # super
    return true if @robot.conds[@cond]
    uri = "/login/login.php"
    url = uri
    params = {:email=>@robot.username,:password=>@robot.password}
    suc = false
    begin
      @browser.request(url, params)
      # @browser.resp.code_type == Net::HTTPFound
      suc = !@browser.location.match(/\/home\/\?uid\=\d*/).nil?
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
  end
end

# garden home action
class GardenHomeAction < BaseAction
  attr :garden, true
  def verify
    # referer: /!house/ranch/index.php
    uri = "/!house/garden/index.php"
    url = uri
    params = nil
    @browser.request(url, params)
    g_verify = @browser.html.match(/g\_verify.*\"(.*)\"\;/)
    @garden[:verify] = g_verify[1] if !g_verify.nil?
    # p_movie = @browser.html.match(/\<param.*name.*movie.*value\=\"(.*)\".*\/\>/)
    # @garden[:movie] = p_movie[1] if !p_movie.nil?
    # self.load_swf
    # puts @browser.html
    # var g_verify = "372249_1062_372249_1245338722_5be6d41192d96932c5bdbb6e2c0a9c50";
    # var g_prevuid = "1095717";
    # var g_nextuid = "372159";
    # var g_fuid = "0";
    # var g_fta = "我";
    # var g_frealname = "我";
    # var g_najax = "0";
  end
  def load_swf(movie = nil)
    # TODO
    # <param name="movie" value="http://img.kaixin001.com.cn/swf/house/garden/garden-42.swf" />
    # movie = @garden[:movie] if movie.nil?
    # @browser.request(movie)
    url = "/interface/i.php"
    params = {"0.13711921153298967" => "", "_" => "", "class" => "house", "id" =>0}
    @browser.request(url, params)
  end
  def newslist(fuid = 0)
    verify = @garden[:verify]
    list = []
    uri = "/house/garden/newslist.php"
    # url = "#{uri}?_=&fuid=#{fuid}&verify=#{verify}"
    url = uri
    params = {:_ => '', :fuid => "#{fuid}", :verify => verify}
    @browser.request(url, params)
    @browser.parse.each do |hash|
      news = {
        'fuid'	=> hash['fuid'],
        'msg'	=> "#{hash['etime']},#{hash['msg']}".gsub(/\<[^\>]*\>/, '')
      }
      @log.p "# news #{news['msg']}"
      list << news
    end
    return list
  end
  def getconf(fuid = 0)
    url = "/!house/!garden/getfriendmature.php"
    @browser.html = nil
    @browser.resp = nil
    @browser.request(url, nil, {:noreferer => true})
    @browser.parse.each do |e|
      @log.p "> gfdm #{@browser.conv(e.to_s)}"
    end
    verify = @garden[:verify]
    conf = []
    r    = "0.5358656840398908"
    uri  = "/!house/!garden//getconf.php"
    url = uri
    params = {:fuid => "#{fuid}", :verify => verify, :r => r}
    @browser.request(url, params)
    puts @browser.resp
    puts @browser.html.length
  end
  def plough
=begin
http://www.kaixin001.com/!house/!garden/plough.php?seedid=0&fuid=0&farmnum=14&verify=372249%5F1062%5F372249%5F1245339396%5Ffdb2d76b898365512e6e82cecbe53603&r=0%2E2879472100175917
Host: www.kaixin001.com
User-Agent: Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.9.0.10) Gecko/2009042523 Ubuntu/8.10 (intrepid) Firefox/3.0.10
Accept: text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8
Accept-Language: en-us,en;q=0.5
Accept-Encoding: gzip,deflate
Accept-Charset: ISO-8859-1,utf-8;q=0.7,*;q=0.7
Keep-Alive: 300
Connection: keep-alive
Cookie: _uid=372249; _email=jhsea3do%40gmail.com; _kx=cdf9b362455c61c3e658a1f407d44ec7_372249; _user=7b82eb587fedb1607e760104950ccc46_372249_1245339404; SERVERID=_srv100-54_
Date: Thu, 18 Jun 2009 15:43:28 GMT
Server: Apache
Cache-Control: max-age=10; private
Expires: Thu, 18 Jun 2009 15:43:38 GMT
Set-Cookie: _user=abfed7187ab5df54247e981e5e8743d2_372249_1245339808; path=/; domain=.kaixin001.com
Vary: Accept-Encoding,User-Agent
Content-Encoding: gzip
Content-Length: 42
Connection: close
Content-Type: text/html; charset=UTF-8
-------------------------------------------
farmnum	14
fuid 	0
r	0.2879472100175917
seedid	0
verify	372249_1062_372249_1245339396_fdb2d76b898365512e6e82cecbe53603
-------------------------------------------
<data><ret>succ</ret></data>
=end
  end
  def myseedlist(page = 1)
    verify = @garden[:verify]
    list = []
    r    = "0.9692404847592115"
    uri  = "/!house/!garden/myseedlist.php"
    url  = "#{uri}?verify=#{verify}&page=#{page}&r=#{r}"
    params = nil
    @browser.request(url, params)
    puts @browser.html
  end
  def getfriendmature
    url = "/!house/!garden/getfriendmature.php"
    @browser.request(url, nil, {:noreferer => true})
    puts @browser.html
  end
  def before
    @robot.account[:garden] = {} if @robot.account[:garden].nil?
    @garden = @robot.account[:garden]
    self.verify() if @garden[:verify].nil?
    self.getconf
    # @garden[:newslist] = self.newslist()
    # self.newslist
    # self.seedlist
  end
  def on
    super
  end
  def after
    @robot.save(@robot.account, 'account')
  end
end

# garden shop action
class GardenShopAction < BaseAction
end

# garden seed action
class GardenSeedAction < BaseAction
end

# garden harvest action
class GardenHarvestAction < BaseAction
end

# garden water action
class GardenWaterAction < BaseAction
end

# garden bugs action
class GardenBugsAction < BaseAction
end

# garden grass action
class GardenGrassAction < BaseAction
end

# farm shop action
class FarmShopAction < BaseAction
end

end
