# actions.rb

module JAVAWS::BOT::ACTION

# reset action
class ResetAction < BaseAction
    def on
    @log.p "! #{@robot} reset now!"
  end
end

# home action
class HomeAction < BaseAction
  def on
    super
    # @browser.request('/mainMenu.html')
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
    uri = "/login.html"
    url = uri
    params = {:j_username=>@robot.username,:j_password=>@robot.password}
    suc = false
    begin
      @browser.request(url, params)
      # @browser.resp.code_type == Net::HTTPFound
      suc = !@browser.location.match(/mainMenu|editProfile/).nil?
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

# send action
class SendAction < BaseAction
  def before
    return if @options[:content].nil?
    sa = SearchAction.new(@robot)
    @options[:students] = []
    sa.find_students({:phone => @options[:phone]}).each do |id|
      @options[:students] << "S#{id}"
    end
  end
  def on
    # return @options[:students].nil? || @options[:students].length < 1
    uri = '/sendSms.html'
    begin
      content = @options[:content]
      content = eval("\"#{content}\"") if content.match(/\#\{.*\}/)
      @options[:content] = content
    rescue
    end
    params = {
      :content => @options[:content], 
      :send => '',
      :recverid => @options[:students]
    }
    @browser.request(uri, params)
  end
end

# search action
class SearchAction < BaseAction
  def find_students(options = nil)
    uri  = '/myStudents.html'
    # expr = "table#infoStudentList td"
    expr = nil
    sels = [:origid, :name, :phone\
               , :classname, :classorigid, :schoolname\
               , :status, :feetype]
    return search(uri, options, sels, expr)
  end
  def search(uri = nil, options = nil, sels = nil, expr = nil)
    results = []
    options = {} if options.nil?
    html_expr  = expr || "table.table td"
    selections = sels
    index, key = nil
    selections.each do |sel|
      index, key = selections.index(sel), options[sel] \
        if !options[sel].nil? 
      break if !key.nil?
    end
    params = {
      'searchkey' => key, 
      'searchindex' => "#{index + 1}",
      'searchsubmit' => 'search'
    }
    @browser.request(uri, params)
    if !@browser.location.nil?
      @browser.request(@browser.location)
      expr = /login\.jsp/
      need_login = !"#{@browser.search("div#detail")}".match(expr).nil?
      # TODO
      puts "! need login => #{need_login}"
    end
    @browser.search(html_expr).each do |e|
      n = e.at("a") || nil
      next if n.nil?
      next if n.inner_html.nil?
      results << n.inner_html.to_i
    end
    # @log.p "# find #{results.join(",")}"
    return results
  end
end

end
