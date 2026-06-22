package com.example.chamati.DataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.chamati.Model.Chamado;
import java.util.ArrayList;
import java.util.List;

public class DataBaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "chamaTI.db";
    private static final int DATABASE_VERSION = 2;

    public static final String TABLE_CHAMADOS = "chamados";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_TITULO = "titulo";
    public static final String COLUMN_DESCRICAO = "descricao";
    public static final String COLUMN_LOCAL = "local";
    public static final String COLUMN_TIPO = "tipo";
    public static final String COLUMN_DATA_CADASTRO = "data_cadastro";
    public static final String COLUMN_STATUS = "status";
    public static final String COLUMN_SOLUCAO = "solucao";
    public static final String COLUMN_IMAGEM_PATH = "imagem_path";
    public static final String COLUMN_PARSE_OBJECT_ID = "parse_object_id";

    public DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_CHAMADOS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_TITULO + " TEXT, " +
                COLUMN_DESCRICAO + " TEXT, " +
                COLUMN_LOCAL + " TEXT, " +
                COLUMN_TIPO + " INTEGER, " +
                COLUMN_DATA_CADASTRO + " TEXT, " +
                COLUMN_STATUS + " TEXT, " +
                COLUMN_SOLUCAO + " TEXT, " +
                COLUMN_IMAGEM_PATH + " TEXT, " +
                COLUMN_PARSE_OBJECT_ID + " TEXT)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE " + TABLE_CHAMADOS + " ADD COLUMN " + COLUMN_IMAGEM_PATH + " TEXT");
            db.execSQL("ALTER TABLE " + TABLE_CHAMADOS + " ADD COLUMN " + COLUMN_PARSE_OBJECT_ID + " TEXT");
        }
    }

    public long inserirChamado(Chamado chamado) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITULO, chamado.getTitulo());
        values.put(COLUMN_DESCRICAO, chamado.getDescricao());
        values.put(COLUMN_LOCAL, chamado.getLocal());
        values.put(COLUMN_TIPO, chamado.getTipo());
        values.put(COLUMN_DATA_CADASTRO, chamado.getDataCadastro());
        values.put(COLUMN_STATUS, chamado.getStatus());
        values.put(COLUMN_SOLUCAO, chamado.getSolucao());
        values.put(COLUMN_IMAGEM_PATH, chamado.getImagemPath());
        values.put(COLUMN_PARSE_OBJECT_ID, chamado.getParseObjectId());
        return db.insert(TABLE_CHAMADOS, null, values);
    }

    public int atualizarChamado(Chamado chamado) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITULO, chamado.getTitulo());
        values.put(COLUMN_DESCRICAO, chamado.getDescricao());
        values.put(COLUMN_LOCAL, chamado.getLocal());
        values.put(COLUMN_TIPO, chamado.getTipo());
        values.put(COLUMN_STATUS, chamado.getStatus());
        values.put(COLUMN_SOLUCAO, chamado.getSolucao());
        values.put(COLUMN_IMAGEM_PATH, chamado.getImagemPath());
        values.put(COLUMN_PARSE_OBJECT_ID, chamado.getParseObjectId());

        return db.update(TABLE_CHAMADOS, values, COLUMN_ID + " = ?",
                new String[]{String.valueOf(chamado.getId())});
    }

    public Chamado getChamadoById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_CHAMADOS, null, COLUMN_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            Chamado chamado = cursorToChamado(cursor);
            cursor.close();
            return chamado;
        }
        return null;
    }

    public List<Chamado> getAllChamados() {
        List<Chamado> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_CHAMADOS + " ORDER BY id DESC", null);

        if (cursor.moveToFirst()) {
            do {
                list.add(cursorToChamado(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    private Chamado cursorToChamado(Cursor cursor) {
        Chamado c = new Chamado();
        c.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));
        c.setTitulo(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITULO)));
        c.setDescricao(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRICAO)));
        c.setLocal(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LOCAL)));
        c.setTipo(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_TIPO)));
        c.setDataCadastro(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATA_CADASTRO)));
        c.setStatus(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STATUS)));
        c.setSolucao(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SOLUCAO)));
        c.setImagemPath(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGEM_PATH)));
        c.setParseObjectId(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PARSE_OBJECT_ID)));
        return c;
    }

    public Chamado getChamadoByParseObjectId(String parseObjectId) {
        if (parseObjectId == null || parseObjectId.isEmpty()) return null;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_CHAMADOS, null, COLUMN_PARSE_OBJECT_ID + "=?",
                new String[]{parseObjectId}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            Chamado chamado = cursorToChamado(cursor);
            cursor.close();
            return chamado;
        }
        if (cursor != null) cursor.close();
        return null;
    }

    public long upsertChamado(Chamado chamado) {
        Chamado existente = getChamadoByParseObjectId(chamado.getParseObjectId());
        if (existente != null) {
            chamado.setId(existente.getId());
            atualizarChamado(chamado);
            return existente.getId();
        } else {
            return inserirChamado(chamado);
        }
    }

    public int getCountByStatus(String status) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_CHAMADOS + " WHERE " + COLUMN_STATUS + "=?", new String[]{status});
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }

    public List<Chamado> getChamadosFiltrados(Integer tipo, String status, String dataIni, String dataFim) {
        List<Chamado> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        StringBuilder query = new StringBuilder("SELECT * FROM " + TABLE_CHAMADOS + " WHERE 1=1");
        List<String> args = new ArrayList<>();

        if (tipo != null) {
            query.append(" AND ").append(COLUMN_TIPO).append(" = ?");
            args.add(String.valueOf(tipo));
        }
        if (status != null && !status.isEmpty()) {
            query.append(" AND ").append(COLUMN_STATUS).append(" = ?");
            args.add(status);
        }

        Cursor cursor = db.rawQuery(query.toString() + " ORDER BY id DESC", args.toArray(new String[0]));

        if (cursor.moveToFirst()) {
            do {
                Chamado chamado = cursorToChamado(cursor);
                
                // Filtro de data em Java (formato dd/MM/yyyy)
                if (dataIni != null && !dataIni.isEmpty() && dataFim != null && !dataFim.isEmpty()) {
                    String dataChamado = chamado.getDataCadastro();
                    if (compararDatas(dataChamado, dataIni) >= 0 && compararDatas(dataChamado, dataFim) <= 0) {
                        list.add(chamado);
                    }
                } else {
                    list.add(chamado);
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    private int compararDatas(String data1, String data2) {
        try {
            String[] d1 = data1.split("/");
            String[] d2 = data2.split("/");
            int ano1 = Integer.parseInt(d1[2]);
            int mes1 = Integer.parseInt(d1[1]);
            int dia1 = Integer.parseInt(d1[0]);
            int ano2 = Integer.parseInt(d2[2]);
            int mes2 = Integer.parseInt(d2[1]);
            int dia2 = Integer.parseInt(d2[0]);
            
            if (ano1 != ano2) return ano1 - ano2;
            if (mes1 != mes2) return mes1 - mes2;
            return dia1 - dia2;
        } catch (Exception e) {
            return 0;
        }
    }
}
