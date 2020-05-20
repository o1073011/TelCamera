package tw.edu.pu.gm.o1073011.telcamera;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.DexterActivity;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.karumi.dexter.listener.single.PermissionListener;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button tel, camera;
        tel = (Button) findViewById(R.id.tel);
        tel.setOnClickListener(this);
        camera = (Button) findViewById(R.id.camera);
        camera.setOnClickListener(this);

        Dexter.withActivity(this)
                .withPermission(android.Manifest.permission.READ_CONTACTS)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        // permission is granted
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        System.exit(0);
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.tel){
            Intent it = new Intent();
            it.setAction(Intent.ACTION_CALL);
            EditText telNo = (EditText) findViewById(R.id.telNo);
            Uri u = Uri.parse("tel:" + telNo.getText().toString());
            it.setData(u);
            startActivity(it);
        }
        else{
            Intent it =  new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            //startActivityForResult(it, 100);
            if (it.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(it, 100);
            }
        }

    }

    Dexter.withActivity(this)
            .withPermissions(android.Manifest.permission.CALL_PHONE, android.Manifest.permission.CAMERA)
                .withListener(new MultiplePermissionsListener() {
        @Override
        public void onPermissionsChecked(MultiplePermissionsReport report) {
            // check if all permissions are granted
            if (report.areAllPermissionsGranted()) {  // 使用者已允許全部權限
            }
            else{
                //權限未全部允許時，關閉對應之按鈕
                for (int i=0;i<report.getDeniedPermissionResponses().size();i++) {
                    String Denied = report.getDeniedPermissionResponses().get(i).getPermissionName();
                    if (Denied.equals("android.permission.CALL_PHONE") ){
                        tel.setEnabled(false);
                    }
                    else if (Denied.equals("android.permission.CAMERA") ) {
                        camera.setEnabled(false);
                    }
                }
                //如果權限被永久拒絕時，跳至設定畫面
                if (report.isAnyPermissionPermanentlyDenied()) {
                    Toast.makeText(MainActivity.this, "您永久拒絕某些權限，無法使用本App全部功能", Toast.LENGTH_LONG).show();
                    Intent it = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                    it.setData(uri);
                    startActivity(it);
                }
                else{
                    Toast.makeText(MainActivity.this, "您未允許全部權限，將無法使用部份功能", Toast.LENGTH_LONG).show();
                }
            }
        }

        @Override
        public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
            permissionToken.continuePermissionRequest();
        }
    })
            .onSameThread()
                .check();

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            ImageView img = (ImageView)findViewById(R.id.img);
            img.setImageBitmap(imageBitmap);
        }
    }


}
