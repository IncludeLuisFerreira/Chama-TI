package com.example.chamati.DataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.chamati.Model.Chamado;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class DataBaseHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "CHAMADOS_TI_INFRA.db";

    public DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable =
                "CREATE TABLE chamados (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "titulo TEXT NOT NULL," +
                        "descricao TEXT," +
                        "local TEXT, " +
                        "tipo INTEGER NOT NULL," +  // 0 - TI   1 - INFRA
                        "data_criacao INTEGER NOT NULL," +
                        "status TEXT DEFAULT 'aberto'" +
                        ")";
        db.execSQL(createTable);
    }

    public long insert(Chamado ch) {
        SQLiteDatabase db = null;
        long id = -1;

        try {
            db = getWritableDatabase();
            ContentValues values = new ContentValues();

            values.put("titulo", ch.getTitulo());
            values.put("descricao", ch.getDescricao());
            values.put("local", ch.getLocal());
            values.put("tipo", ch.getTipo());
            values.put("data_criacao", ch.getDataCadastro());
            values.put("status", ch.getStatus());

            id = db.insert("chamados", null, values);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db != null && db.isOpen()) {
                db.close();
            }
        }

        return id; // retorna o ID gerado ou -1 se erro
    }

    public ArrayList<Chamado> getList() {
        ArrayList<Chamado> lista = new ArrayList<>();

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(
                "chamados", null, null, null,
                null, null, "id DESC");  // Ordena por id do mais recente primeiro

        if (cursor.moveToFirst()) {
            do {
                long id = cursor.getLong(cursor.getColumnIndexOrThrow("id"));
                String titulo = cursor.getString(cursor.getColumnIndexOrThrow("titulo"));
                String descricao = cursor.getString(cursor.getColumnIndexOrThrow("descricao"));
                String local = cursor.getString(cursor.getColumnIndexOrThrow("local"));
                int tipo = cursor.getInt(cursor.getColumnIndexOrThrow("tipo"));
                long dataCriacao = cursor.getLong(cursor.getColumnIndexOrThrow("data_criacao"));
                String status = cursor.getString(cursor.getColumnIndexOrThrow("status"));

                Chamado c = new Chamado(id, titulo, descricao, local, tipo, dataCriacao, status);
                lista.add(c);

            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        // Log para debugging (opcional)
        for (int i = 0; i < lista.size(); i++) {
            Log.i("CHAMADO", "ID: " + lista.get(i).getId());
            Log.i("CHAMADO", "Título: " + lista.get(i).getTitulo());
            Log.i("CHAMADO", "Descrição: " + lista.get(i).getDescricao());
            Log.i("CHAMADO", "Local: " + lista.get(i).getLocal());
            Log.i("CHAMADO", "Tipo: " + (lista.get(i).getTipo() == 0 ? "TI" : "INFRA"));
            Log.i("CHAMADO", "Data: " + new Date(lista.get(i).getDataCadastro()));
            Log.i("CHAMADO", "Status: " + lista.get(i).getStatus());
            Log.i("CHAMADO", "----------");
        }

        return lista;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public Chamado getChamadoById(long id) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query("chamados", null, "id = ?", 
                new String[]{String.valueOf(id)}, null, null, null);
        
        Chamado c = null;
        if (cursor.moveToFirst()) {
            String titulo = cursor.getString(cursor.getColumnIndexOrThrow("titulo"));
            String descricao = cursor.getString(cursor.getColumnIndexOrThrow("descricao"));
            String local = cursor.getString(cursor.getColumnIndexOrThrow("local"));
            int tipo = cursor.getInt(cursor.getColumnIndexOrThrow("tipo"));
            long dataCriacao = cursor.getLong(cursor.getColumnIndexOrThrow("data_criacao"));
            String status = cursor.getString(cursor.getColumnIndexOrThrow("status"));
            
            c = new Chamado(id, titulo, descricao, local, tipo, dataCriacao, status);
        }
        cursor.close();
        db.close();
        return c;
    }

    public boolean updateChamado(long id, String status, String solucao) {
        SQLiteDatabase db = getWritableDatabase();
        
        String sql = "ALTER TABLE chamados ADD COLUMN solucao TEXT";
        try {
            db.execSQL(sql);
        } catch (Exception e) {
            // Coluna já existe
        }
        
        ContentValues values = new ContentValues();
        values.put("status", status);
        values.put("solucao", solucao);
        
        int rows = db.update("chamados", values, "id = ?", new String[]{String.valueOf(id)});
        db.close();
        return rows > 0;
    }

    public ArrayList<Chamado> getChamadosFiltrados(String status, long data) {
        ArrayList<Chamado> lista = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        
        String selection = "";
        ArrayList<String> args = new ArrayList<>();
        
        if (status != null) {
            selection = "status = ?";
            args.add(status);
        }
        
        if (data != -1) {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(data);
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            long inicio = cal.getTimeInMillis();
            
            cal.set(Calendar.HOUR_OF_DAY, 23);
            cal.set(Calendar.MINUTE, 59);
            cal.set(Calendar.SECOND, 59);
            long fim = cal.getTimeInMillis();
            
            if (!selection.isEmpty()) selection += " AND ";
            selection += "data_criacao BETWEEN ? AND ?";
            args.add(String.valueOf(inicio));
            args.add(String.valueOf(fim));
        }
        
        Cursor cursor = db.query("chamados", null, 
                selection.isEmpty() ? null : selection, 
                args.isEmpty() ? null : args.toArray(new String[0]), 
                null, null, "id DESC");
        
        if (cursor.moveToFirst()) {
            do {
                long id = cursor.getLong(cursor.getColumnIndexOrThrow("id"));
                String titulo = cursor.getString(cursor.getColumnIndexOrThrow("titulo"));
                String descricao = cursor.getString(cursor.getColumnIndexOrThrow("descricao"));
                String local = cursor.getString(cursor.getColumnIndexOrThrow("local"));
                int tipo = cursor.getInt(cursor.getColumnIndexOrThrow("tipo"));
                long dataCriacao = cursor.getLong(cursor.getColumnIndexOrThrow("data_criacao"));
                String st = cursor.getString(cursor.getColumnIndexOrThrow("status"));
                
                lista.add(new Chamado(id, titulo, descricao, local, tipo, dataCriacao, st));
            } while (cursor.moveToNext());
        }
        
        cursor.close();
        db.close();
        return lista;
    }
}