package com.example.tamoor.apcentral;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    /**
     * Methodo onCreate. se inicializa los botones y edit text y pongo el listner.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Se inicializan los botones.
        Button siguiente = (Button) findViewById(R.id.btSiguiente);
        siguiente.setOnClickListener(this);

        Button salir = (Button) findViewById(R.id.btSalir);
        salir.setOnClickListener(this);

        //se inicializa edit text
        EditText etMatricula = (EditText) findViewById(R.id.etMat);
        TextView tvMat = (TextView) findViewById(R.id.tvMat);

        etMatricula.setOnClickListener(this);

    }


    /**
     * Metodo on click, en este methodo estan definidos las tareas que el programa va a hacer
     * con los botones o funciones que estan en layouts. El usuario tendra que selecionar un opcion
     * y segon eso si seleciona el primero entonces obligatoriamente tiene que poner matricula
     * tambien , mientras en otros opcion no es obligatorio.
     * @param view
     */
    @Override
    public void onClick(View view) {

        //Si el boton de siguiente ha sido apretado.
        if (view.getId() == R.id.btSiguiente) {
            RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radioGroup);

            //hago un get de intent
            Intent intent = getIntent();
            EditText etMatricula = (EditText) findViewById(R.id.etMat);
            //Recojo el valor de edit text.
            String matricula = etMatricula.getText().toString();

            //Switch case, cuando el usuario seleciona un radioboton para segon el boton
            //elegido por el usuario el programa va a hacer una funcion.
            switch (radioGroup.getCheckedRadioButtonId()) {
                //En el caso de que el usuario selecione el boton de mostrar ruta de un bus.
                case R.id.rdBus:

                    //Compruebo que en este caso la matricula es obligatorio.
                    //El usuario debe escrbir matricula.
                    if (matricula.isEmpty() || matricula.length() == 0 || matricula.equals("") || matricula == null) {
                        //si esta null, muestra un toast diciendo que tiene que poner algo en ese campo.
                        Toast.makeText(this, "Introduce una matricula", Toast.LENGTH_SHORT).show();
                        //Se ciera el intent automaticamente y vuevle a ejecutar.
                        finish();
                        startActivity(intent);
                    } else {

                        //Si cuample los pasos, entonces eso de lleva a mapsactivity, le paso matricula tambien
                        Intent i = new Intent(this, MapsActivity.class);
                        i.putExtra("matricula", matricula);
                        //Se inicia.
                        startActivity(i);
                    }
                    break;


                //En el caso de que el usuario ha selecionado mostrar todas los autobuses.
                case R.id.rdTodosBuses:
                    //Se abre otro intent.
                    Intent i = new Intent(this, MapsActivityTodos.class);
                    startActivity(i);
                    break;

                //Por defecto muestra un toast en el caso de que el usuario no haya selecionado ningun
                //opcion, que tiene que selecionar un opcion.
                default:
                    Toast.makeText(this, "Elije una opcion", Toast.LENGTH_SHORT).show();


            }

            //startActivity(intent);
            //si el usuario desea salir, picando al buton de salir podra salir.
        } else if (view.getId() == R.id.btSalir) {
            finish();
        }


    }


}
