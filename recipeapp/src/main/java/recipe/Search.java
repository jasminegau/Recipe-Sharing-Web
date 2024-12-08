package recipe;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;

import com.google.gson.Gson;

import org.apache.lucene.store.ByteBuffersDirectory;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class Search
 */
@WebServlet("/Search")
public class Search extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Search() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//code learned from: //https://www.lucenetutorial.com/techniques/indexing-databases.html
		
		//establish settings and tools
		response.setContentType("application/json");
		PrintWriter out = response.getWriter();
		Gson gson = new Gson();
		
		StandardAnalyzer analyzer = new StandardAnalyzer();
		Directory index = new ByteBuffersDirectory();
		IndexWriterConfig config = new IndexWriterConfig(analyzer);
		IndexWriter w = new IndexWriter(index, config);
		
		//connecting to database and adding every recipe into a lucene document to index
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection con = DriverManager.getConnection("jdbc:mysql://localhost/RecipeApp", "root", "root");
			PreparedStatement stmt = con.prepareStatement("SELECT r.id, r.title, r.category, u.username FROM recipes r, users u WHERE r.author = u.id");
	        ResultSet rs = stmt.executeQuery();
	       while(rs.next()) {
	    	   	Document doc = new Document();
	    	   	//textfields allow for tokenization while storedfields are only for storage
	    	   	doc.add(new StoredField("id", rs.getInt("id")));
			    doc.add(new TextField("title", rs.getString("title"), Field.Store.YES));
			    doc.add(new TextField("category", rs.getString("category"), Field.Store.YES)); 
			    doc.add(new StoredField("author", rs.getString("username")));
			    w.addDocument(doc);
	       }
		} catch (SQLException error) {
			System.out.println(error);
		}
		catch (ClassNotFoundException error) {
			System.out.println(error);
		}
		finally {
			w.commit();
			w.close();
		}
		ArrayList<Recipe> recipes = new ArrayList<Recipe>();
		try {
			//getting search and creating queries with search term, user can search from title or category
			String searchTerm = request.getParameter("query").toLowerCase();
			Query titleQ = new QueryParser("title", analyzer).parse(searchTerm);
			Query categoryQ = new QueryParser("category", analyzer).parse(searchTerm);
			
	        //building search query
	        BooleanQuery.Builder builder = new BooleanQuery.Builder();
	        builder.add(titleQ, BooleanClause.Occur.SHOULD);
	        builder.add(categoryQ, BooleanClause.Occur.SHOULD);
	        BooleanQuery booleanQuery = builder.build();
	        
	        //executing search
	        DirectoryReader reader = DirectoryReader.open(index);
	        IndexSearcher searcher = new IndexSearcher(reader);
	        TopDocs docs = searcher.search(booleanQuery, 30);
	        ScoreDoc[] results = docs.scoreDocs;
	        System.out.println("Found " + results.length + " hits.");
	        
	        //parsing results
	        for (int i = 0; i < results.length; i++) {
	        	int docId = results[i].doc;
	            @SuppressWarnings("deprecation")
				Document d = searcher.doc(docId);
	            recipes.add(new Recipe(Integer.parseInt(d.get("id")), d.get("title"), d.get("category"), "", d.get("author")));
	        }
		}
		catch(IOException error) {
			System.out.println(error);
		}
		catch(ParseException error) {
			System.out.println(error);
		}
		finally {
			out.print(gson.toJson(recipes));
			out.flush();
		}
	}
}
