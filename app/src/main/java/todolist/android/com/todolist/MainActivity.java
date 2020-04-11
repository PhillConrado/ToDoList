package todolist.android.com.todolist;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.IntegerRes;
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
    private Button   botaoAdicionar;
    private ListView listaDeTarefas;
    private SQLiteDatabase bancoDados;

    private ArrayAdapter<String> itensAdaptador;
    private ArrayList<String> itens;
    private ArrayList<Integer> ids;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            //Recuperar componentes da tela
            textoTarefa = (EditText) findViewById(R.id.textoId);
            listaDeTarefas = (ListView) findViewById(R.id.listViewId);
            botaoAdicionar = (Button) findViewById(R.id.botaoAdicionarId);

            //Bancos de Dados
            bancoDados = openOrCreateDatabase("apptarefas", MODE_PRIVATE, null);

            //Criação da tabela tarefas
            bancoDados.execSQL("CREATE TABLE IF NOT EXISTS tarefas" +
                    "(id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    " tarefa VARCHAR) ");

            //Evento de OnClick botão Adicionar
            botaoAdicionar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String textoDigitado = textoTarefa.getText().toString();
                    salvarTarefa(textoDigitado);
                }
            });
//            Remover com um click rápido
//            listaDeTarefas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                @Override
//                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
//                    removerTarefa( ids.get(position));
//                }
//            });

            //Remover com um click longo
            listaDeTarefas.setLongClickable(true);
            listaDeTarefas.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    removerTarefa( ids.get( position ) );
                    return true;
                }
            });

            //Recuperar tarefas
            recuperarTarefas();

        }catch(Exception e){
            e.printStackTrace();
        }

    }

    private void salvarTarefa(String texto){
        try {
            if(texto.equals("")){
                Toast.makeText(MainActivity.this, "Digite uma tarefa", Toast.LENGTH_SHORT).show();
            }else{
                bancoDados.execSQL("INSERT INTO tarefas(tarefa) VALUES ('" + texto + "')");
                Toast.makeText(MainActivity.this, "Tarefa salva com sucesso!", Toast.LENGTH_SHORT).show();
                recuperarTarefas();
                textoTarefa.setText("");
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void recuperarTarefas(){
        try{
            //Recuperar lista de Tarefas
            Cursor cursor = bancoDados.rawQuery("SELECT * FROM tarefas ORDER BY id DESC", null);

            //Recuperar ids das colunas
            int indiceColunaId = cursor.getColumnIndex("id");
            int indiceColunaTarefa = cursor.getColumnIndex("tarefa");

            //Criar adaptador
            itens = new ArrayList<String>();
            ids = new ArrayList<Integer>();
            itensAdaptador = new ArrayAdapter<String>(getApplicationContext(),
                    android.R.layout.simple_list_item_1,
                    android.R.id.text1,
                    itens);
            listaDeTarefas.setAdapter(itensAdaptador);

            //Listar Tarefas
            cursor.moveToFirst();
            while(cursor != null){

                Log.i("Resultado - ", "Tarefa : "+cursor.getString(indiceColunaTarefa));
                itens.add(cursor.getString(indiceColunaTarefa));
                ids.add(Integer.parseInt( cursor.getString(indiceColunaId) ));
                cursor.moveToNext();
            }

        }catch (Exception e){
            e.printStackTrace();

        }
    }

    private void removerTarefa(Integer id){
        try{
            bancoDados.execSQL("DELETE FROM tarefas WHERE id =" + id);
            Toast.makeText(MainActivity.this, "Tarefa removida com sucesso!", Toast.LENGTH_SHORT).show();
            recuperarTarefas();

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
