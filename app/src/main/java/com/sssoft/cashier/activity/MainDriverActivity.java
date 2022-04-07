package com.sssoft.cashier.activity;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.sssoft.cloudpos.R;

public class MainDriverActivity extends Activity {

    private Button scan;
    private Button print;
    private String  printStr = "<center><large>商户存根</large></center><br><center><small>-----------------------------------------</small></center><br><center><normal>测试专用2</normal></center><br><center><large>预授权:0.02</large></center><br><center><small>交易成功</small></center><br><center><small>-----------------------------------------</small></center><br><left><normal>支付账号:134861********0350</normal></left><br><left><normal>支付方式:聚合支付</normal></left><br><left><large>担保号:0329116</large></left><br><left><normal>交易时间:2022/03/30 15:53:57</normal></left><br><left><normal>商户编号:104650053110061</normal></left><br><left><normal>终端编号:BCDEFHIJKLMN-_ABCDE1</normal></left><br><left><normal>操作员号:BCDEFHIJKLMN-_ABCDE1</normal></left><br><left><normal>商户流水号(预授权撤销使用):</normal></left><br><center><normal><qrCode>722033000873213</qrCode></normal></center><br><center><normal>722033000873213</normal></center><br><left><normal>授权号(预授权完成使用):</normal></left><br><center><normal><qrCode>738256</qrCode></normal></center><br><center><normal>738256</normal></center><br><center><small>------------------------------------------</small></center><br><left><normal>用户签字:</normal></left><br><center><large>           </large></center><br><center><large>           </large></center><br><center><small>-----------------------------------------</small></center><br><left><normal>商户可以扫码获取商户流水号进行完成，解冻和撤销操作</normal></left><br><cut><center><large>用户存根</large></center><br><center><small>-----------------------------------------</small></center><br><center><normal>测试专用2</normal></center><br><center><large>预授权:0.02</large></center><br><center><small>交易成功</small></center><br><center><small>-----------------------------------------</small></center><br><left><normal>支付账号:134861********0350</normal></left><br><left><normal>支付方式:聚合支付</normal></left><br><left><large>担保号:0329116</large></left><br><left><normal>交易时间:2022/03/30 15:53:57</normal></left><br><left><normal>商户编号:104650053110061</normal></left><br><left><normal>终端编号:BCDEFHIJKLMN-_ABCDE1</normal></left><br><left><normal>操作员号:BCDEFHIJKLMN-_ABCDE1</normal></left><br><left><normal>商户流水号(预授权撤销使用):</normal></left><br><center><normal><qrCode>722033000873213</qrCode></normal></center><br><center><normal>722033000873213</normal></center><br><left><normal>授权号(预授权完成使用):</normal></left><br><center><normal><qrCode>738256</qrCode></normal></center><br><center><normal>738256</normal></center><br><center><small>------------------------------------------</small></center><br><left><normal>商户可以扫码获取商户流水号进行完成，解冻和撤销操作</normal></left><br>";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_driver);
        scan = findViewById(R.id.scan);
        print = findViewById(R.id.print);
        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        print.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

    }
}