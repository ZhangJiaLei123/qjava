package com.demo.telnet;

import blxt.qjava.qtelnet.QTelnetClient;

public class TelnetTest {

    static String hostIp = "192.168.2.56";
    static int port = 23;
    static String username = "root";
    static String password = "root";

    public static void main(String[] args) throws Exception {
        QTelnetClientTest telnetClientTest = new QTelnetClientTest();
        telnetClientTest.test();

    }

    public static class QTelnetClientTest implements QTelnetClient.OnTelnetClientListener {

        public void test() throws InterruptedException {
            QTelnetClient telnetOperator = new QTelnetClient();
            telnetOperator.setHostIp(hostIp);
            telnetOperator.setPort(port);
            telnetOperator.setUsername(username);
            telnetOperator.setPassword(password);
            telnetOperator.setPrompt("#");
            telnetOperator.connect();

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if(telnetOperator.login()){
                System.out.println("登录成功");
            }
            else{
                System.out.println("登录失败");
                return;
            }

            // 登陆成功后, 再添加回调监听
            telnetOperator.setOnTelnetClientListener(this);
            telnetOperator.onReadThread();

            telnetOperator.write("pwd");
            Thread.sleep(1000);
            telnetOperator.write("ls");
            Thread.sleep(500);
            telnetOperator.write("/apps/app/test4/1653529652109_testddr3");
//            String res = telnetOperator.sendCommand("mkdir test");
//            System.out.println("结果:" + res);
//            res = telnetOperator.sendCommand("ls");
//            System.out.println("结果:" + res);

            //telnetOperator.write("debug :1235 /apps/test5/application");
  //          telnetOperator.write("vi /root/startup.sh");
        }

        @Override
        public void onReceiver(String tag, String msg) {
            System.out.println("收到数据: " + msg );
        }

        @Override
        public void onConnect(String tag, boolean isconnect) {

        }

        @Override
        public void onDisConnect(String tag, boolean isconnect) {

        }

        @Override
        public void onLogin(String tag, boolean isLogin) {

        }

        @Override
        public void onGetDate(String tag, byte[] data) {
        //    System.out.print((char)data[0]);
        }
    }



}
