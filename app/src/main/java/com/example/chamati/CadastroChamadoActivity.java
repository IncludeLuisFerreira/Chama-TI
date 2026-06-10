package com.example.chamati;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import com.example.chamati.Cloud.ChamadoCloudManager;
import com.example.chamati.DataBase.DataBaseHelper;
import com.example.chamati.Model.Chamado;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CadastroChamadoActivity extends AppCompatActivity {

    private EditText etTitulo, etLocal, etDescricao;
    private AutoCompleteTextView spinnerStatus;
    private MaterialButtonToggleGroup toggleTipo;
    private TextView tvDataAtual;
    private MaterialButton btnRegistrar, bntLimpar, btnCapturarFoto;
    private ImageView ivFotoPreview;
    private DataBaseHelper dbHelper;
    private ChamadoCloudManager cloudManager;
    private String currentPhotoPath;

    private final ActivityResultLauncher<Uri> cameraLauncher = registerForActivityResult(
            new ActivityResultContracts.TakePicture(),
            success -> {
                if (success) {
                    ivFotoPreview.setVisibility(ImageView.VISIBLE);
                    ivFotoPreview.setImageURI(Uri.fromFile(new File(currentPhotoPath)));
                }
            });

    private final ActivityResultLauncher<String> permissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            granted -> {
                if (granted) {
                    dispatchTakePictureIntent();
                } else {
                    Toast.makeText(this, "Permissão de câmera necessária", Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_chamado);

        dbHelper = new DataBaseHelper(this);
        cloudManager = new ChamadoCloudManager();

        etTitulo = findViewById(R.id.etTitulo);
        etLocal = findViewById(R.id.etLocal);
        etDescricao = findViewById(R.id.etDescricao);
        toggleTipo = findViewById(R.id.toggleTipo);
        tvDataAtual = findViewById(R.id.tvDataAtual);
        btnRegistrar = findViewById(R.id.btnRegistrar);
        bntLimpar = findViewById(R.id.btnLimpar);
        spinnerStatus = findViewById(R.id.spinnerStatusCadastro);
        btnCapturarFoto = findViewById(R.id.btnCapturarFoto);
        ivFotoPreview = findViewById(R.id.ivFotoPreview);

        String[] statuses = {"Aberto", "Em Andamento", "Concluído"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, statuses);
        spinnerStatus.setAdapter(adapter);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> {
            String titulo = etTitulo.getText().toString().trim();
            String local = etLocal.getText().toString().trim();
            String desc = etDescricao.getText().toString().trim();

            if (!titulo.isEmpty() || !local.isEmpty() || !desc.isEmpty()) {
                new androidx.appcompat.app.AlertDialog.Builder(this)
                        .setTitle("Sair sem salvar?")
                        .setMessage("Você começou a preencher o chamado. Se sair agora, os dados serão perdidos.")
                        .setPositiveButton("Sair", (dialog, which) -> finish())
                        .setNegativeButton("Continuar editando", null)
                        .show();
            } else {
                finish();
            }
        });

        String currentData = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
        tvDataAtual.setText(currentData);

        btnCapturarFoto.setOnClickListener(v -> {
            if (checkSelfPermission(android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                dispatchTakePictureIntent();
            } else {
                permissionLauncher.launch(android.Manifest.permission.CAMERA);
            }
        });

        btnRegistrar.setOnClickListener(v -> {
            String titulo = etTitulo.getText().toString().trim();
            String local = etLocal.getText().toString().trim();
            String descricao = etDescricao.getText().toString().trim();

            if (titulo.isEmpty() || local.isEmpty() || descricao.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
                return;
            }

            if (toggleTipo.getCheckedButtonId() == -1) {
                Toast.makeText(this, "Selecione o tipo do chamado", Toast.LENGTH_SHORT).show();
                return;
            }

            int tipo = (toggleTipo.getCheckedButtonId() == R.id.btnInfra) ? 1 : 0;

            String statusSelecionado = spinnerStatus.getText().toString();
            String statusDB = "aberto";
            if (statusSelecionado.equals("Em Andamento")) statusDB = "andamento";
            else if (statusSelecionado.equals("Concluído")) statusDB = "fechado";

            Chamado novo = new Chamado(titulo, descricao, local, tipo, currentData, statusDB);
            novo.setSolucao("");
            novo.setImagemPath(currentPhotoPath);
            long id = dbHelper.inserirChamado(novo);

            if (id != -1) {
                novo.setId((int) id);
                Toast.makeText(this, "Chamado registrado com sucesso!", Toast.LENGTH_SHORT).show();

                cloudManager.salvarChamadoCloud(novo, new ChamadoCloudManager.SyncCallback() {
                    @Override
                    public void onSuccess(String parseObjectId) {
                        novo.setParseObjectId(parseObjectId);
                        dbHelper.atualizarChamado(novo);
                    }

                    @Override
                    public void onError(String errorMessage) {
                        // Falha silenciosa - dados ja salvos localmente
                    }
                });

                finish();
            } else {
                Toast.makeText(this, "Erro ao registrar chamado", Toast.LENGTH_SHORT).show();
            }
        });

        bntLimpar.setOnClickListener(v -> {
            etTitulo.setText("");
            etLocal.setText("");
            etDescricao.setText("");
            toggleTipo.clearChecked();
            spinnerStatus.setText("Aberto", false);
            currentPhotoPath = null;
            ivFotoPreview.setVisibility(ImageView.GONE);
            ivFotoPreview.setImageDrawable(null);
            etTitulo.requestFocus();
            Toast.makeText(this, "Campos limpos", Toast.LENGTH_SHORT).show();
        });
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = createImageFile();
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.chamati.fileprovider",
                        photoFile);
                currentPhotoPath = photoFile.getAbsolutePath();
                cameraLauncher.launch(photoURI);
            }
        }
    }

    private File createImageFile() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "chamado_" + timeStamp;
        File storageDir = getFilesDir();
        File imageDir = new File(storageDir, "chamado_images");
        if (!imageDir.exists()) {
            imageDir.mkdirs();
        }
        try {
            return File.createTempFile(imageFileName, ".jpg", imageDir);
        } catch (IOException e) {
            Toast.makeText(this, "Erro ao criar arquivo de imagem", Toast.LENGTH_SHORT).show();
            return null;
        }
    }
}
