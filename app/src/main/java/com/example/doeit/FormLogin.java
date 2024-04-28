package com.example.doeit;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class            FormLogin extends AppCompatActivity {

    private EditText edit_email,edit_senha;
    private Button bt_entrar;
    private ProgressBar progressBar;
    private TextView telaCadastroDoador;
    private TextView telaCadastroDonatario;
    String[] mensagens = {"Preencha todos os campos"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.form_login);

        IniciarComponents();

        telaCadastroDoador.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FormLogin.this,FormCadastroDoador.class);
                startActivity(intent);
            }
        });

        telaCadastroDonatario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FormLogin.this,CadastroDonatario.class);
                startActivity(intent);
            }
        });

        bt_entrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = edit_email.getText().toString();
                String senha = edit_senha.getText().toString();

                if (email.isEmpty() || senha.isEmpty()){
                    Snackbar snackbar = Snackbar.make(v,mensagens[0],Snackbar.LENGTH_SHORT);
                    snackbar.setBackgroundTint(Color.WHITE);
                    snackbar.setTextColor(Color.RED);
                    snackbar.show();
                } else {
                    AutenticarDoador(v);
                }
            }
        });
    }

    private void AutenticarDoador(View view) {
        String email = edit_email.getText().toString();
        String senha = edit_senha.getText().toString();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, senha)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            assert user != null;
                            String usuarioID = user.getUid();


                            DocumentReference referenceDoador = db.collection("Usuarios").document(usuarioID);
                            referenceDoador.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot document = task.getResult();
                                        if (document.exists()) {
                                            progressBar.setVisibility(View.VISIBLE);
                                            new Handler().postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    TelaPrincipal1();
                                                }
                                            }, 2000);
                                        }
                                    }
                                }
                            });

                            DocumentReference referenceDonatario = db.collection("Donatarios").document(usuarioID);
                            referenceDonatario.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot document = task.getResult();
                                        if (document.exists()) {
                                            progressBar.setVisibility(View.VISIBLE);
                                            new Handler().postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    TelaPrincipal2();
                                                }
                                            }, 2000);
                                        }
                                    } else {
                                        Log.d(TAG, "Erro ao obter documento: ", task.getException());
                                    }
                                }
                            });
                        } else {
                            Snackbar snackbar = Snackbar.make(view, "Erro ao logar usuário", Snackbar.LENGTH_SHORT);
                            snackbar.setBackgroundTint(Color.WHITE);
                            snackbar.setTextColor(Color.RED);
                            snackbar.show();
                        }
                    }
                });
    }


    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser usuarioAtual = FirebaseAuth.getInstance().getCurrentUser();

        if(usuarioAtual != null){
            String usuarioID = usuarioAtual.getUid();

            DocumentReference donatariosRef = FirebaseFirestore.getInstance().collection("Donatarios").document(usuarioID);
            DocumentReference doadoresRef = FirebaseFirestore.getInstance().collection("Usuarios").document(usuarioID);

            donatariosRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    DocumentSnapshot document = task.getResult();
                    if(document.exists()){
                        TelaPrincipal2();
                    }else{
                        doadoresRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if(task.isSuccessful()){
                                    TelaPrincipal1();
                                }
                            }
                        });

                    }
                }
            });
        }
    }

    protected void onStart2() {
        super.onStart();

        FirebaseUser usuarioAtual = FirebaseAuth.getInstance().getCurrentUser();

        if (usuarioAtual != null){
            TelaPrincipal2();
        }
    }

    private void TelaPrincipal1(){
        Intent intent = new Intent(FormLogin.this, DoadorActivity.class);
        startActivity(intent);
        finish();
    }

    private void TelaPrincipal2(){
        Intent intent = new Intent(FormLogin.this, HomeDonatario.class);
        startActivity(intent);
        finish();
    }

    private void IniciarComponents(){
        telaCadastroDoador = findViewById(R.id.telaCadastroDoador);
        telaCadastroDonatario = findViewById(R.id.telaCadastroDonatario);
        edit_email = findViewById(R.id.edit_email);
        edit_senha = findViewById(R.id.edit_senha);
        bt_entrar = findViewById(R.id.bt_entrar);
        progressBar = findViewById(R.id.progressbar);
    }
}