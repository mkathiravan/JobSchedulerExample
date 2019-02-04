package com.example.kabali.jobschedulerexample;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private JobScheduler mScheduler;
    private static final int JOB_ID = 0;
    Button schedulejob,canceljob;
    private Switch mDeviceIdleSwitch, mDeviceChargingSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        schedulejob = (Button)findViewById(R.id.schedule_job);
        canceljob = (Button)findViewById(R.id.cancel_job);

        mDeviceIdleSwitch = (Switch)findViewById(R.id.idleSwitch);
        mDeviceChargingSwitch = (Switch)findViewById(R.id.chargingSwitch);

        schedulejob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scheduleJob(v);
            }
        });

        canceljob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                cancelJob(v);

            }
        });
    }

    public void scheduleJob(View view) {

        mScheduler = (JobScheduler)getSystemService(JOB_SCHEDULER_SERVICE);

        RadioGroup networkOptions = (RadioGroup)findViewById(R.id.network_options);

        int selectedNetworkId = networkOptions.getCheckedRadioButtonId();

        int selectedNetworkOption = JobInfo.NETWORK_TYPE_NONE;

        switch (selectedNetworkId)
        {
            case R.id.noNetwork:
                selectedNetworkOption = JobInfo.NETWORK_TYPE_NONE;
                break;

            case R.id.anyNetwork:
                selectedNetworkOption = JobInfo.NETWORK_TYPE_ANY;
                break;

            case R.id.wifiNetwork:
                selectedNetworkOption = JobInfo.NETWORK_TYPE_UNMETERED;
                break;
        }


        ComponentName serviceName = new ComponentName(getPackageName(),NotificationJobService.class.getName());
        JobInfo.Builder builder = new JobInfo.Builder(JOB_ID,serviceName)
                        .setRequiredNetworkType(selectedNetworkOption)
                        .setRequiresDeviceIdle(mDeviceIdleSwitch.isChecked())
                        .setRequiresCharging(mDeviceChargingSwitch.isChecked());

        boolean constrainset = (selectedNetworkOption != JobInfo.NETWORK_TYPE_NONE) || mDeviceChargingSwitch.isChecked() || mDeviceIdleSwitch.isChecked();

        if(constrainset)
        {
            JobInfo myjobInfo = builder.build();
            mScheduler.schedule(myjobInfo);
            Toast.makeText(this,"Job Scheduled",Toast.LENGTH_SHORT).show();
        }
        else
        {
            Toast.makeText(this,"Please Select atleast one constraint",Toast.LENGTH_SHORT).show();
        }





    }

    public void cancelJob(View view) {

        if(mScheduler != null)
        {
            mScheduler.cancelAll();
            mScheduler = null;
            Toast.makeText(this,"Jobs Cancelled",Toast.LENGTH_SHORT).show();
        }
    }
}
