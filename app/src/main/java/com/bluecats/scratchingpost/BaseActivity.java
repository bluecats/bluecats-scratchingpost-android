package com.bluecats.scratchingpost;

import android.content.*;
import android.os.*;
import android.support.v7.app.*;

public class BaseActivity extends AppCompatActivity {
    protected ApplicationPermissions mPermissions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPermissions = new ApplicationPermissions(BaseActivity.this);
        mPermissions.verifyPermissions();
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (mPermissions != null) {
                mPermissions.verifyPermissions();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (mPermissions != null) {
            mPermissions.onRequestPermissionResult(requestCode, permissions, grantResults);
        }
    }
}
