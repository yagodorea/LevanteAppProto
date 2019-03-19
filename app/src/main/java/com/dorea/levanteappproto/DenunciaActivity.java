package com.dorea.levanteappproto;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.dorea.levanteappproto.utils.Occurrence;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class DenunciaActivity extends AppCompatActivity {

    private static final String TAG = "DenunciaActivity";

    EditText txtContexto;
    CheckBox tipo1, tipo2, tipo3, tipo4, tipo5, tipo6, tipo7;
    Button btnGPS, btnAnexar, btnEnviar;
    ImageButton btnFoto, btnVideo;
    ImageView imgPreview;
    ProgressBar progressBar;
    Uri imgUri;
    private String mCurrentFilePath;
    private String dbIndex;
    private String imgRef;
    private int idForca = 0;
    private double lat, lon;
    private boolean dataAquired = false;

    // Firebase objects
    FirebaseDatabase mFirebaseDatabase;
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;
    DatabaseReference myRef;
    StorageReference mStorageRef;

    private static final double DEFAULT_LAT = -22.012251;
    private static final double DEFAULT_LON = -47.900835;
    private static final int MAP_REQUEST_CODE = 1307;
    private static final int ATTACH_REQUEST_CODE = 9307;
    private static final int PHOTO_REQUEST_CODE = 1993;
    private static final int VIDEO_REQUEST_CODE = 1994;
    private static final int CODE_MEDIA_PHOTO = 1;
    private static final int CODE_MEDIA_VIDEO = 2;
    private static final int CODE_MEDIA_ATTACH = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_denuncia);
        Log.d(TAG, "Shazam! ->onCreate: inicio.");

        imgPreview = findViewById(R.id.imgUploaded);

        // Atribuição dos botões
        // Mídias
        btnAnexar = findViewById(R.id.btnAnexar);
        btnFoto = findViewById(R.id.btnFoto);
        btnVideo = findViewById(R.id.btnVideo);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);
        // Enviar
        btnEnviar = findViewById(R.id.btnEnviar);
        btnEnviar.setEnabled(false);
        // Botao GPS
        btnGPS = findViewById(R.id.btnGPS);
        btnGPS.setBackgroundResource(R.drawable.toggle_off);
        // Contexto
        txtContexto = findViewById(R.id.txtContexto);

        // Tipos de irregularidades
        tipo1 = findViewById(R.id.chkTipo1);    // Violência abusiva
        tipo2 = findViewById(R.id.chkTipo2);    // Impedimento de gravação
        tipo3 = findViewById(R.id.chkTipo3);    // Agente sem identificação
        tipo4 = findViewById(R.id.chkTipo4);    // Uso indevido de arma de fogo
        tipo5 = findViewById(R.id.chkTipo5);    // Abuso de autoridade
        tipo6 = findViewById(R.id.chkTipo6);    // Humilhação ou tortura
        tipo7 = findViewById(R.id.chkTipo7);    // Suborno ou extorsão

        // Declarar objeto de referência ao BD, para acessa-lo
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();

        // Referência para armazenamento de mídia
        mStorageRef = FirebaseStorage.getInstance().getReference();

        // Botão para enviar os dados para o BD
        Log.d(TAG, "Shazam! ->onCreate: Listener do botão enviar.");
        btnEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG,"Shazam! ->onClick: Attempting to add object to database.");

                btnEnviar.setActivated(false);

                if (dataAquired) {
                    // Escrever todos os outros dados
                    imgUpload();
                }
                else {
                    setResult(RESULT_CANCELED);
                    finish();
                }
            }
        });

        // Listener do botão Anexar
        btnAnexar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnMedia(CODE_MEDIA_ATTACH);
            }
        });

        // Listener do botão Foto
        btnFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnMedia(CODE_MEDIA_PHOTO);
            }
        });

        // Listener do botão Video
        btnVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnMedia(CODE_MEDIA_VIDEO);
            }
        });

        /////////////// MAPA E LOCALIZAÇÃO ///////////////////

        // Listener do botão de GPS
        Log.d(TAG, "Shazam! ->onCreate: Listener do botão de GPS.");
        btnGPS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(DenunciaActivity.this, MapsActivity.class);
                startActivityForResult(i, MAP_REQUEST_CODE);
            }
        });

       // Dropdown tipo de força policial
        Spinner forca = findViewById(R.id.forcaSpinner);
        ArrayAdapter<CharSequence> adapterForca = ArrayAdapter.createFromResource(this,
                R.array.forcas,R.layout.my_spinner);
        adapterForca.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        forca.setAdapter(adapterForca);
        forca.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d(TAG, "Shazam! ->onItemSelected: idForca = " + i);
                idForca = i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                idForca = 0;
            }
        });
    }

    //
    //
    //
    // MOST IMPORTANT FUNCION >>>>>~~~~~~~~~~~~~~>
    //
    //
    //
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "Shazam! ->onActivityResult: resultCode: " + resultCode + "requestCode: " + requestCode);
        if (requestCode == MAP_REQUEST_CODE && data != null) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "Localização obtida!", Toast.LENGTH_SHORT).show();;
                if (data.hasExtra("markLat") && data.hasExtra("markLon")) {
                    lat = data.getDoubleExtra("markLat", DEFAULT_LAT);
                    lon = data.getDoubleExtra("markLon", DEFAULT_LON);
                    dataAquired = true;
                    btnEnviar.setEnabled(true);
                    Log.d(TAG, "Shazam! ->onActivityResult: retornando do mapa. lat:" + lat + ", lon:" + lon);
                }
                btnGPS.setBackgroundResource(R.drawable.toggle_on);
            }
        }
        else if (requestCode == ATTACH_REQUEST_CODE && resultCode == RESULT_OK) {
            Log.d(TAG, "Shazam! ->onActivityResult: imgUri: " + imgUri);

            // Setar preview
            Bitmap bm = BitmapFactory.decodeFile(mCurrentFilePath);
            imgPreview.setImageBitmap(bm);
        }
        else if (requestCode == PHOTO_REQUEST_CODE && resultCode == RESULT_OK) {
            Log.d(TAG, "Shazam! ->onActivityResult: imgUri: " + imgUri);

            // Setar preview
            Bundle extras = data.getExtras();
            if (extras != null) {
                Bitmap bm = (Bitmap) extras.get("data");
                imgPreview.setImageBitmap(bm);
            }
        }
        else if (requestCode == VIDEO_REQUEST_CODE && resultCode == RESULT_OK) {
            Log.d(TAG, "Shazam! ->onActivityResult: imgUri: " + imgUri);

            // Pegar preview do video
            Bitmap bm = ThumbnailUtils.createVideoThumbnail(mCurrentFilePath,MediaStore.Video.Thumbnails.MICRO_KIND);
            if (bm != null) {
                imgPreview.setImageBitmap(bm);
            }
        }
        if (resultCode == RESULT_CANCELED) {
            toastMessage("Cancelado!");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "Shazam! ->onResume: chamada.");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(RESULT_CANCELED);
        finish();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "Shazam! ->onDestroy: destruindo DenunciaActivity.");
        super.onDestroy();
    }

    @Override
    public void onStart() {
        Log.d(TAG, "Shazam! ->onStart: começando DenunciaActivity.");
        super.onStart();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null)
            loginAnonimo();
    }

    private void loginAnonimo() {
        /////////////// GERENCIAMENTO DE AUTH ///////////////////
        // Autenticação anônima
        mAuth.signInAnonymously()
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "Shazam! ->signInAnonymously:success");
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInAnonymously:failure", task.getException());
                            Toast.makeText(DenunciaActivity.this, "Autenticação anonima falhou.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }

    @Override
    public void onStop() {
        Log.d(TAG, "Shazam! ->onStop: parando DenunciaActivity.");
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    public void btnMedia(int codeMedia) {
        Intent i;
        switch (codeMedia) {
            case CODE_MEDIA_PHOTO :{ i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); break; }
            case CODE_MEDIA_VIDEO :{ i = new Intent(MediaStore.ACTION_VIDEO_CAPTURE); break; }
            case CODE_MEDIA_ATTACH :{
                i = new Intent(Intent.ACTION_GET_CONTENT);
                i.setType("image/*");
                break;
            }
            default : { Log.d(TAG, "Shazam! ->btnMedia: codeMedia errado!"); i = new Intent(); }
        }

        // Se certificar de que existe uma atividade da câmera pra tratar
        if (i.resolveActivity(getPackageManager()) != null) {
            // Criar arquivo que vai ser a midia
            File mediaFile = null;
            try {
                mediaFile = createFile(codeMedia);
            } catch (IOException e) {
                e.printStackTrace();
                Log.d(TAG, "Shazam! ->btnMedia: erro criando arquivo da midia");
            }
            if (mediaFile != null) {
                imgUri = FileProvider.getUriForFile(this,
                                                    "com.example.android.fileprovider",
                                                            mediaFile);
                i.putExtra(MediaStore.EXTRA_OUTPUT, imgUri);
                Log.d(TAG, "Shazam! ->btnMedia: comecar atividade");
                imgRef = imgUri.toString();
                Log.d(TAG, "Shazam! ->btnMedia: imgRef = " + imgRef);
                switch (codeMedia) {
                    case CODE_MEDIA_PHOTO :{
                        startActivityForResult(i, PHOTO_REQUEST_CODE);
                        break;
                    }case CODE_MEDIA_VIDEO :{
                        startActivityForResult(i, VIDEO_REQUEST_CODE);
                        break;
                    }case CODE_MEDIA_ATTACH :{
                        startActivityForResult(i, ATTACH_REQUEST_CODE);
                        break;
                    }default : {
                        Log.d(TAG, "Shazam! ->btnMedia: codeMedia errado!");
                    }
                }
            }
        } else {
            Log.d(TAG, "Shazam! ->btnPhotoClick: nao existe atividade da camera");
        }
    }

    public void imgUpload() {
        Log.d(TAG, "Shazam! ->imgUpload: entrou");
        dataAquired = false;
        if (imgUri != null) {

            Toast.makeText(DenunciaActivity.this,"Anexando!",Toast.LENGTH_SHORT).show();

            Log.d(TAG, "Shazam! ->imgUpload: entrou, imgUri not null");
            progressBar.setVisibility(View.VISIBLE);
            btnEnviar.setEnabled(false);

            final StorageReference ref = mStorageRef.child(imgRef);
            ref.putFile(imgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Log.d(TAG, "Shazam! ->onSuccess: upload com sucesso");
                    Toast.makeText(DenunciaActivity.this,"Anexado com sucesso",Toast.LENGTH_SHORT).show();
                    dataAquired = true;
                    btnEnviar.setEnabled(true);
                    progressBar.setVisibility(View.INVISIBLE);
                    imgRef = taskSnapshot.getDownloadUrl().toString();
                    sendData();
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "Shazam! ->onFailure: falha no upload");
                    Toast.makeText(DenunciaActivity.this,"Anexo falhou!" + e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            });

        }else {
            Log.d(TAG, "Shazam! ->imgUpload: entrou, imgUri null");
            sendData();
        }
    }

    private void sendData() {

        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        btnEnviar.setActivated(true);

        // Opaque ID for occurrence
        UUID newID = UUID.randomUUID();

        DatabaseReference occId = myRef.child("occurrences").child(newID.toString());

        Occurrence newOccurrence = new Occurrence();

        newOccurrence.setImgRef(imgRef);

        newOccurrence.setTimestamp(new Date(System.currentTimeMillis()).toLocaleString());

        String[] forcas = getResources().getStringArray(R.array.forcas);
        if (idForca <= 4){
            newOccurrence.setForca(forcas[idForca]);
        }

        newOccurrence.setViolenciaAbusiva(tipo1.isChecked());
        newOccurrence.setImpedimentoGravacao(tipo2.isChecked());
        newOccurrence.setAgenteSemIdentificacao(tipo3.isChecked());
        newOccurrence.setUsoIndevidoArmaDeFogo(tipo4.isChecked());
        newOccurrence.setAbusoAutoridade(tipo5.isChecked());
        newOccurrence.setHumilhacaoTortura(tipo6.isChecked());
        newOccurrence.setSubornoExtorsao(tipo7.isChecked());

        if (txtContexto.getText() != null) {
            newOccurrence.setContexto(txtContexto.getText().toString());
        }

        newOccurrence.setLat(lat);
        newOccurrence.setLon(lon);

        occId.setValue(newOccurrence);
        setResult(RESULT_OK);
        finish();
    }

    private File createFile(int codeMedia) throws IOException {
        // Criar nome do arquivo
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String fileName, suffix;
        File storageDir;
        switch (codeMedia) {
            case CODE_MEDIA_PHOTO: {
                fileName = "JPEG_" + timeStamp + "_";
                suffix = ".jpg";
                storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                break;
            }
            case CODE_MEDIA_VIDEO: {
                fileName = "MPEG_" + timeStamp + "_";
                suffix = ".mp4";
                storageDir = getExternalFilesDir(Environment.DIRECTORY_MOVIES);
                break;
            }
            case CODE_MEDIA_ATTACH: {
                fileName = "JPEG_" + timeStamp + "_";
                suffix = ".jpg";
                storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                break;
            }
            default: {
                fileName = "";
                suffix = ".error";
                storageDir = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
                Log.d(TAG, "Shazam! ->createFile: codeMedia errado!");
            }
        }
        File file = File.createTempFile(
                fileName,
                suffix,
                storageDir
        );

        mCurrentFilePath = file.getAbsolutePath();
        return file;
    }


    private void toastMessage(String message) {
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }
}
