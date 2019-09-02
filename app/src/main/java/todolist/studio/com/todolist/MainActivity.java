package todolist.studio.com.todolist;

import android.app.Activity;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends Activity {

    private EditText textoTarefa;
    private Button botaoAdicionar;
    private ListView listTarefas;

    private SQLiteDatabase bancoDados;

    private ArrayAdapter<String> itensAdaptador;
    private ArrayList<String> itens;
    private ArrayList<Integer>  ids;

    private AlertDialog.Builder dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {

            //Recuperar componentes
            textoTarefa = (EditText) findViewById(R.id.textoId);
            botaoAdicionar = (Button) findViewById(R.id.botaoId);
            listTarefas = (ListView) findViewById(R.id.listViewId);


            //Banco dados
            bancoDados = openOrCreateDatabase("apptarefas", MODE_PRIVATE, null);

            //tabela tarefas
            bancoDados.execSQL("CREATE TABLE IF NOT EXISTS tarefas(id INTEGER PRIMARY KEY AUTOINCREMENT, tarefa VARCHAR ) ");

            botaoAdicionar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //recebendo texto que foi digitado pelo usuario
                    String textoDigitado = textoTarefa.getText().toString();
                    salvarTarefa(textoDigitado);
                }
            });

            //clique longo para deletar
            listTarefas.setLongClickable(true);
            listTarefas.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {

                     final int pos = position;
                    //criacao alert dialog
                    dialog = new AlertDialog.Builder(MainActivity.this);

                    //configurar titulo
                    dialog.setTitle("Delete");

                    //configurar a mensagem
                    dialog.setMessage("Tem certeza que deseja deletar a tarefa? ");

                    //definir se pode ser cancelado
                    dialog.setCancelable(false);

                    //atribuindo icone
                    dialog.setIcon(android.R.drawable.ic_delete);

                    //botao negativo
                    dialog.setNegativeButton("Não",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    //Toast.makeText(MainActivity.this, "Botão Não pressionado", Toast.LENGTH_LONG).show();
                                }
                            });

                    //botao positivo
                    dialog.setPositiveButton("Sim",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    removerTarefa(ids.get( pos ));

                                }
                            });

                    dialog.create();
                    dialog.show();

                    return true;
                }
            });

            //Listar tarefas
            recuperarTarefas();


        }catch(Exception e){
            e.printStackTrace();
        }

    }

    private void salvarTarefa (String texto) {
        try {
            if(texto.equals("")) {
                Toast.makeText(MainActivity.this, "Digite uma tarefa", Toast.LENGTH_SHORT).show();
            }
            else{
                //INSERT
                bancoDados.execSQL("INSERT INTO tarefas (tarefa) VALUES ('" + texto + "') ");
                Toast.makeText(MainActivity.this, "Tarefa salva com sucesso.", Toast.LENGTH_SHORT).show();
                recuperarTarefas();
                textoTarefa.setText("");
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    private void recuperarTarefas(){
        try{
            //Recuperar as tarefas
            Cursor cursor = bancoDados.rawQuery("SELECT * FROM tarefas ORDER BY id DESC", null);

            //recuperar os ids das colunas
            int indiceColunaId = cursor.getColumnIndex("id");
            int indiceColunaTarefa = cursor.getColumnIndex("tarefa");

            //instancia itens e ids
            itens = new ArrayList<String>();
            ids = new ArrayList<Integer>();



            //adaptador
            itensAdaptador = new ArrayAdapter<String>(getApplicationContext(),
                    android.R.layout.simple_expandable_list_item_2,
                    android.R.id.text2,
                   itens);
            listTarefas.setAdapter(itensAdaptador);


            //listar as tarefas
            cursor.moveToFirst();
            while ( cursor != null ){

                Log.i("Resultado - ", "id Tarefa: " + cursor.getString( indiceColunaId )  +" Tarefa: " + cursor.getString( indiceColunaTarefa ));
                itens.add(cursor.getString( indiceColunaTarefa ));
                ids.add(Integer.parseInt(cursor.getString( indiceColunaId )));
                cursor.moveToNext();
            }

        }
        catch(Exception e){
            e.printStackTrace();
        }

    }
    private void removerTarefa(Integer id){
        try{
            bancoDados.execSQL("DELETE FROM tarefas WHERE id= "+ id);
            Toast.makeText(MainActivity.this, "Tarefa removida.", Toast.LENGTH_LONG).show();
            recuperarTarefas();
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

}
