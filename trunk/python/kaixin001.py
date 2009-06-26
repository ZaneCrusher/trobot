#!/usr/bin/env python
# -*- coding:utf-8 -*-
# coded by Daniel King

import re, time, thread, webbrowser
from Tkinter import *
import urllib, urllib2, cookielib, json

class Kaixin(object):

    def __init__(self, app):
        self.app = app
        self.signed_in = False
        self.cj = cookielib.LWPCookieJar()
        try:
           self.cj.revert('kaixin.coockie')
        except:
            None
        self.opener = urllib2.build_opener(urllib2.HTTPCookieProcessor(self.cj))
        urllib2.install_opener(self.opener)

    def signin(self):
        r = self.opener.open('http://www.kaixin001.com/home/')
        if r.geturl() == 'http://www.kaixin001.com/?flag=1&url=%2Fhome%2F':
            print 'Need logging on!'
            params = {'email':self.app.email, 'password':self.app.passwd, 'remember':1}
            req = urllib2.Request(
                'http://www.kaixin001.com/login/login.php',
                urllib.urlencode(params)
            )
            r = self.opener.open(req)
            if r.geturl() == 'http://www.kaixin001.com/home/':
                print 'Logged on successfully!'
                self.cj.save('kaixin.coockie')
                self.signed_in = True
        else:
            print 'ok!'
            self.signed_in = True

    def monitor(self):
        if not self.signed_in:
            self.signin()
        button = self.app.buttons['monitor']
        if self.signed_in:
            r = self.opener.open('http://www.kaixin001.com/app/app.php?aid=1062&url=garden/index.php')
            m = re.search('var g_verify = "(.+)";', r.read())
            verify = m.group(1)

            self.is_monitoring = True
            button['text'] = u'停止'
            while self.is_monitoring:
                print time.strftime('%y/%m/%d %H:%M:%S')
                req = urllib2.Request(
                    'http://www.kaixin001.com/house/garden/getfriendmature.php',
                    urllib.urlencode({'_':'','verify':verify})
                )

                r = self.opener.open(req)
                self.app.data = json.loads(r.read())
                app.list.delete(0, END)
                for f in self.app.data:
                    self.app.list.insert(END, f['realname'])
                time.sleep(30)

        button['text'] = u'开始'
        print 'Monitoring is stopped.'

    def stop_monitoring(self):
        self.is_monitoring = False

class App(Tk):

    def __init__(self):
        Tk.__init__(self)
        self.data = []
        self.email = ''
        self.passwd = ''
        self.interval = 30
        self.kaixin = Kaixin(self)
        self.font = 'Tahoma 9'
        self.geometry('240x200')
        self.title(u'开心网果实探测仪')

        self.btnpane = PanedWindow(self)
        self.btnpane.pack(expand = 0, side = 'bottom')

        buttons = [[self.on_monitor, u'开始', 'monitor'],
                [self.on_config, u'设置', 'config'],
                [self.on_exit, u'退出', 'exit']]
        self.buttons = {}

        for btn in buttons:
            button = Button(self.btnpane, text = btn[1], font = self.font)
            self.buttons[btn[2]] = button
            button.bind('<ButtonRelease-1>', btn[0])
            self.btnpane.add(button)

        self.list = Listbox(self, font = self.font)
        self.list.pack(expand = 1, fill = 'both', side = 'top')
        self.list.bind('<Double-ButtonRelease-1>', self.on_list_dblclick)

    def on_monitor(self, event):
        button = self.buttons['monitor']
        if button['text'] == u'开始':
            thread.start_new_thread(self.kaixin.monitor, ())
        else:
            self.kaixin.stop_monitoring()

    def on_config(self, event):
        win = ConfigWin(self)

    def on_list_dblclick(self, event):
        s = self.list.curselection()
        if len(s) == 1:
            uid = self.data[int(s[0])][u'uid']
            webbrowser.open('http://www.kaixin001.com/app/app.php?'
                'aid=1062&url=garden/index.php&_lgmode=pri&fuid='+str(uid));

    def on_exit(self, event):
        self.quit()

class ConfigWin(Toplevel):
    def __init__(self, app):
        Toplevel.__init__(self, app)
        self.app = app
        self.title(u'设置')
        Label(self, text = u'电邮:', font = app.font).grid(row = 0, column = 0)
        self.email = Entry(self)
        self.email.grid(row = 0, column = 1)
        Label(self, text = u'密码:', font = app.font).grid(row = 1, column = 0)
        self.passwd = Entry(self, show = '*')
        self.passwd.grid(row = 1, column = 1)
        Label(self, text = u'间隔:', font = app.font).grid(row = 2, column = 0)
        self.interval = Entry(self, text = '30')
        self.interval.grid(row = 2, column = 1)

        self.email.insert(0, app.email)
        self.passwd.insert(0, app.passwd)
        self.interval.insert(0, str(app.interval))

        btnpane = PanedWindow(self)
        btnpane.grid(row = 3, column = 0, columnspan = 2)
        btn_cancel = Button(btnpane, text = u'取消', font = app.font)
        btn_cancel.grid(row = 3, column = 0, columnspan = 2)
        btn_ok = Button(btnpane, text = u'确定', font = app.font)
        btn_ok.grid(row = 3, column = 0, columnspan = 2)
        btnpane.add(btn_cancel)
        btnpane.add(btn_ok)

        btn_ok.bind('<ButtonRelease-1>', self.on_ok)
        btn_cancel.bind('<ButtonRelease-1>', self.on_cancel)

        self.focus_set()
        self.grab_set()
        self.wait_window()

    def on_ok(self, event):
        self.app.email = self.email.get()
        self.app.passwd = self.passwd.get()
        self.app.interval = int(self.interval.get())
        self.destroy()

    def on_cancel(self, event):
        self.destroy()

app = App()
app.mainloop()
