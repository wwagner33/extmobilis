package com.paulo.android.solarmobile;

import android.app.Activity;
import android.content.ContentValues;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;


		/*
		 * Lista os Tópicos do fórum. Se não houver post, deve existir um textview com a mensagem "Não há postagens
		 * nesse fórum"	A lista de posts é uma lista customizada que, no momento , possui 3 elementos em cada list item
		 * 1.A imagem da pessoa, Deve existir uma imagem vazia para os que não possuem imagem cadastrada.
		 * 2.O nome do tópico. Por enquanto não há limite de caracteres.
		 * 3. Uma prévia da mensagem do post em questão, também não há limite de caracteres.
		 * 
		 * Falta implementar um limite de posts por página e quando, chegar ao final, carragarem mais posts automaticamente
		 * assim como a aplicação do gmail. Por enquanto não há limite de posts na página.
		 * 
		 * O layout final não deve conter a imagem do usuário, apenas na View da mensagem completa, juntamente com as
		 * respostas da mensagem 
		 * 
		 * 
		 * */

public class ListaPosts extends Activity {
	PostAdapter adapter;
	ListView listaPosts;
	ContentValues[] ForumContent;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ForumContent = new ContentValues[30];
		for (int i=0;i<ForumContent.length;i++) {
			ForumContent[i] = new ContentValues();
			ForumContent[i].put("ForumName", "Forum"+i);
			ForumContent[i].put("ForumContent", "Simulando o conteúdo de uma lista do tópico, estou escrevendo tanto porque o teclado daqui é bom de escrever" +
					"			");
			
		}
		
		setContentView(R.layout.post);
		listaPosts = (ListView) findViewById(R.id.listposts);
		adapter = new PostAdapter(this,ForumContent);
		listaPosts.setAdapter(adapter);
		
		
	}
	

}
