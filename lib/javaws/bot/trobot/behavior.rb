# tasks.rb

class Behavior
  include JAVAWS::BOT::BEHAVIOR
  attr :timer, true
  attr :world, true
  attr :spieler, true
  def actions
    return [:reset, :show, :home \
      , :dorf1, :dorf2, :login, :logout \
      , :market ]
  end
  def timer(stime = nil)
    @timer = TRAVIAN::Timer.new if @timer.nil?
    @timer.stime = stime
    return @timer
  end
  def spieler
    @spieler = TRAVIAN::Spieler.new if @spieler.nil?
    return @spieler
  end
  def world
    @world = TRAVIAN::World.new if @world.nil?
    return @world
  end
end

module TRAVIAN
  BUILDS = {
    :lumber	 => {:gid => 1},
    :clay	 => {:gid => 2},
    :iron	 => {:gid => 3},
    :crop	 => {:gid => 4},
    :sawmill	 => {:gid => 5},
    :brickyard	 => {:gid => 6},
    :ironFoundry => {:gid => 7},
    :grainMill	 => {:gid => 8},
    :bakery	 => {:gid => 9},
    :warehouse	 => {:gid => 10},
    :granary	 => {:gid => 11},
    :blacksmith	 => {:gid => 12},
    :armoury	 => {:gid => 13},
    :tournamentSquare	 => {:gid => 14},
    :mainBuilding	 => {:gid => 15},
    :rallyPoint	 => {:gid => 16},
    :marketplace	 => {:gid => 17},
    :embassy	 => {:gid => 18},
    :barracks	 => {:gid => 19},
    :stable	 => {:gid => 20},
    :workshop	 => {:gid => 21},
    :academy	 => {:gid => 22},
    :cranny	 => {:gid => 23},
    :townhall	 => {:gid => 24},
    :residence	 => {:gid => 25},
    :palace	 => {:gid => 26},
    :treasury	 => {:gid => 27},
    :tradeOffice	 => {:gid => 28},
    :greatBarrack	 => {:gid => 29},
    :greatStable	 => {:gid => 30},
    :citywall	 => {:gid => 31},
    :earthwall	 => {:gid => 32},
    :palisade	 => {:gid => 33},
    :stonemason	 => {:gid => 34},
    :brewery	 => {:gid => 35},
    :trapper	 => {:gid => 36},
    :herosMansion	 => {:gid => 37},
    :greatWarehouse	 => {:gid => 38},
    :greatGranary	 => {:gid => 39},
    :WW	 => {:gid => 40},
    :horsedt	 => {:gid => 41}
  }

  class Timer
    attr :ltime, true #localtime
    attr :stime, true #servertime
    def stime=(stime = nil)
      @stime = str2time(stime)
      @ltime = Time.new
      return @stime
    end
    def now
      offset = @ltime - @stime
      return (Time.new + offset)
    end
    def time(offset = nil, stime = nil)
      stime   = @stime if stime.nil?
      stime   = str2time(stime) 
      offset  = 0 if offset.nil?
      seconds = 3600 * offset
      time    = stime + seconds
      return time
    end
    def time2str(time = nil)
      time = @stime if time.nil?
      time = Time.new if time.nil?
      return time.strftime('%Y-%m-%d %H:%M:%S')
    end
    def str2time(strtime = nil)
      return Time.new if strtime.nil?
      return strtime if strtime.class == Time
      strdate = Time.new.strftime('%Y-%m-%d')
      strtime = "#{strdate} #{strtime}" if strtime.match(/(\d+)\:(\d+)\:(\d+)/)
      m = strtime.match(/(\d+)\-(\d+)\-(\d+)\ (\d+)\:(\d+)\:(\d+)/)
      t = Time.local(m[1], m[2], m[3], m[4], m[5], m[6])
      return t
    end
  end
 
  class Resource
    attr :c, true # current
    attr :m, true # max
    attr :i, true # increase
    def desc
      return self.class.to_s.downcase
    end
    def itime
      neg = (i < 0)
      r = m - c 
      r = c if neg
      sprintf("%.3f", r.to_f/i)
    end
    def to_s
      return "#{c}/#{m}, #{i}/h, #{itime}h"
    end
  end
  
  class Lumber < Resource
  end

  class Clay < Resource
  end

  class Iron < Resource
  end

  class Crop < Resource
  end

  class Karte
    TYPES = [:t6, :t9, :t15, :lumber5, :clay5, :iron5, :crop5]
    attr :x, true # pos x
    attr :y, true # pos y
    attr :t, true # type
    attr :v, true # is village
    attr :c, true # param c
    attr :d, true # param d or id
    def to_pos
      return "(N/A|N/A)" if @x.nil? || @y.nil?
      return "(#{@x}|#{@y})"
    end
  end

  class Build
    attr :id, true
    attr :gid, true
    attr :lv, true
    attr :name, true
    def to_s
      return "#{id}, #{name} #{lv}"
    end
  end

  class Village
    attr :k, true
    attr :id, true
    attr :name, true
    attr :builds, true
    attr :resources, true
    attr :capital, true
    attr :market, true
    attr :sel, true
    attr :ktype, true
    def to_s
      f = @sel ? "X" : "O"
      t = @ktype || "??"
      k = @k || Karte.new
      return "#{f} [#{t}] #{@id}, #{k.to_pos}, #{@name}"
    end
=begin
    # TODO
    def update(v = nil)
      return if v.nil?
      k = v.k
      id = v.id
      name = v.name
      builds.merge v.builds
      # resources.merge v.resources
      v.resources.each do |k, r|
        @resources[k] = r if !r.nil? && !r.i.nil?
      end
      capital = v.capital
      sel = v.sel
      ktype = v.ktype if !v.ktype.nil?
    end
=end
    def resources
      @resources = {
        :lumber  => Lumber.new,
        :clay    => Clay.new,
        :iron    => Iron.new,
        :crop    => Crop.new
      } if @resources.nil?
      return @resources
    end
    def builds
      @builds = {} if @builds.nil?
      return @builds
    end
    def res
      return resources
    end
  end

  class Spieler
    attr :uid, true
    attr :villages, true
    def villages
      @villages = {} if @villages.nil?
      return @villages
    end
    def village
      @villages.each do |did, v|
        return v if v.sel
      end
    end
    def next_did
      r = nil
      f = false
      2.times do
        break if !r.nil?
        @villages.each do |did, v|
          r = did if f
          break if !r.nil?
          f = v.sel
        end
      end
      return r
    end
  end

  class Allianz
  end

  class Market < Build
    attr :total, true
    attr :away, true
    attr :home, true
    attr :carry, true
    def to_s
      return "market #{home}/#{total}, [#{carry}], lv#{lv}"
    end
    # trade to NPC
    def trade(res, summe, max123, max4)
      rts = [:w, :c, :i, :r]
      sum = 0
      emp = 0
      rts.each do |t|
        i = rts.index(t)
        s = res[i]
        sum = sum + s.to_i if !s.nil?
        emp = emp + 1 if s.nil?
      end
      ave = (summe - sum) / emp
      res.collect!{|s| s = ave if s.nil?; s = s if !s.nil?}
      return average(res, summe, max123, max4)
    end
    # total
    def total(res)
      return res[0] + res[1] + res[2] + res[3]
    end
    # average 
    def average(res, summe, max123, max4)
      while(true)
        res4 = res[3]
        res123 = [res[0],res[1],res[2]]
        rst = summe - total(res)
        ave = rst / 4
        eva = rst - (ave * 4)
        sum = 0
        i = 0
        res123.each do |s|
          z = 0
          o = s + ave
          # o > max123
          z = o - max123 if o > max123
          o = o - z
          # o < 0
          z = z - (0 - o) if o < 0
          o = 0 if o < 0
          if (o + eva) <= max123
            z = z + eva
            o = o + eva
            eva = 0
          end
          sum = sum + z
          res123[i] = o
          i = i + 1
        end
        ser4 = res4 + ave + eva
        sum  = sum + (ser4 - max4) if ser4 > max4
        ser4 = max4 if ser4 > max4
        res  = [res123, ser4].flatten
        # puts res.join("|")
        # puts summe
        # puts sum
        break if sum == 0
      end
      return res
    end
  end

  class Dorf1
  end

  class Dorf2
  end

  class Dorf3
  end

  class Build
  end
 
  class A2b
  end

  class World
    attr :kartes, true
    attr :villages, true
    attr :allianz, true
    attr :spielers, true
    def initialize
      @kartes = {}
      @villages = {}
      @allianz = {}
      @spielers = {}
    end
  end
end
