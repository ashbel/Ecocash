package com.fis.ecocash.ecocash;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;


public class PaymentFragment extends Fragment {

    Button pay_merchant,pay_bill,send_money,cash_out;
    EditText merchant_code, merchant_amount,biller_code,biller_amount,biller_account,agent_code,agent_amount,send_number,send_amount;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_payment, container, false);

        pay_bill = (Button) view.findViewById(R.id.paybill);
        pay_merchant = (Button) view.findViewById(R.id.paymerchant);
        send_money = (Button) view.findViewById(R.id.sendmoney);
        cash_out = (Button) view.findViewById(R.id.cashout);


        pay_bill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // custom dialog
                final Dialog dialog = new Dialog(getContext());
                dialog.setContentView(R.layout.pay_bill);
                Button dialogButton = (Button) dialog.findViewById(R.id.btn_bill_submit);

                biller_code = (EditText) dialog.findViewById(R.id.bill_code_input);
                biller_amount = (EditText) dialog.findViewById(R.id.bill_amount_input);
                biller_account = (EditText) dialog.findViewById(R.id.bill_account_input);

                dialogButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String code_str = biller_code.getText().toString();
                        String amount_str = biller_amount.getText().toString();
                        String account_str = biller_amount.getText().toString();
                        dialog.dismiss();

                        String ussd = "*151*2*1*" +code_str+"*"+amount_str+"*"+account_str +Uri.encode("#");
                        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                            return;
                        }
                     startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + ussd)));


                    }
                });

                dialog.show();

            }
        });

        pay_merchant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = new Dialog(getContext());
                dialog.setContentView(R.layout.pay_merchant);
                Button dialogButton = (Button) dialog.findViewById(R.id.btn_merchant_submit);

                merchant_code = (EditText) dialog.findViewById(R.id.merchant_code_input);
                merchant_amount = (EditText) dialog.findViewById(R.id.merchant_amount_input);

                dialogButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String code_str = merchant_code.getText().toString();
                        String amount_str = merchant_amount.getText().toString();
                        //String account_str = biller_amount.getText().toString();
                        dialog.dismiss();

                        String ussd = "*151*2*2*" +code_str+"*"+amount_str+Uri.encode("#");
                        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
                            return;
                        }
                        startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + ussd)));
                    }
                });

                dialog.show();
            }
        });

        send_money.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = new Dialog(getContext());
                dialog.setContentView(R.layout.send_money);
                Button dialogButton = (Button) dialog.findViewById(R.id.btn_send_money_submit);

                send_number = (EditText) dialog.findViewById(R.id.send_money_input);
                send_amount = (EditText) dialog.findViewById(R.id.send_money_amount_input);

                dialogButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String code_str = send_number.getText().toString();
                        String amount_str = send_amount.getText().toString();
                        dialog.dismiss();
                        String ussd ="";
                        if(amount_str == null || amount_str.isEmpty())
                        {
                            ussd = "*151*1*1*" + code_str + Uri.encode("#");
                        }
                        else {
                            ussd = "*151*1*1*" + code_str + "*" + amount_str + Uri.encode("#");
                        }
                        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
                            return;
                        }
                        startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + ussd)));
                    }
                });

                dialog.show();

            }
        });

        cash_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = new Dialog(getContext());
                dialog.setContentView(R.layout.cash_out);
                Button dialogButton = (Button) dialog.findViewById(R.id.btn_agent_submit);

                agent_code = (EditText) dialog.findViewById(R.id.agent_code_input);
                agent_amount = (EditText) dialog.findViewById(R.id.agent_amount_input);

                dialogButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                         String agent_code_str = agent_code.getText().toString();
                         String agent_amount_str = agent_amount.getText().toString();
                         dialog.dismiss();

                        String ussd = "*151" + Uri.encode("#");
                        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
                            return;
                        }
//                startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + ussd)));


                    }
                });

                dialog.show();
            }
        });

        return view;
    }


}
