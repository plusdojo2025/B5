package servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import dao.CheeseMusicDao;
import dao.CheeseMusicPhraseDao;
import dao.CheesePhraseDao;
import dto.CheeseMusic;
import dto.CheeseMusicPhrase;
import dto.CheesePhrase;
import dto.CheeseUser;


@WebServlet("/CheeseEditMusicServlet")
public class CheeseEditMusicServlet  extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpSession session = request.getSession();
		CheeseUser user = (CheeseUser)session.getAttribute("loginUser");
		if (user == null) {
			response.sendRedirect(request.getContextPath() + "/CheeseLoginServlet");
			return;
		}
		// musicを作成したuserIdとログイン済みのuserIdが違うならログイン画面へ
		// musicIdの取得
		int musicId = Integer.parseInt(request.getParameter("id"));
		int userId = user.getId();
		// 曲データの取得
		CheeseMusicDao musicDao = new CheeseMusicDao();
		CheeseMusic music = musicDao.select(musicId);
		 // セッションスコープのuserIdとmusicのuserIdの比較
		if (userId != music.getUserId()) {
			// 違うならログイン画面へ
			response.sendRedirect(request.getContextPath() + "/CheeseLoginServlet");
			return;
		}
		
		String result = (String) session.getAttribute("result");
		if (result != null) {
			request.setAttribute("result", result);
			session.removeAttribute("result");  // 1回だけ表示
		}
		
		List<CheesePhrase> phraseList;
		CheesePhraseDao phraseDao =  new CheesePhraseDao();
		phraseList = phraseDao.select(new ArrayList<String>(), new ArrayList<String>(), "", userId);
		request.setAttribute("phraseList", phraseList);
			
		CheeseMusicPhraseDao musicPhraseDao = new CheeseMusicPhraseDao();	
		// music_phrase_tableからデータ取得
		List<CheeseMusicPhrase> assignedMusicPhraseList = musicPhraseDao.select(musicId);
		// phraseテーブルからデータ取得
		List<CheesePhrase>  assignedPhraseList = phraseDao.select(assignedMusicPhraseList);
		
		// jspへセット
		request.setAttribute("assignedMusicPhraseList", assignedMusicPhraseList);
		request.setAttribute("assignedPhraseList", assignedPhraseList);
		request.setAttribute("music", music);
		System.out.println(musicId);
		
		// 曲編集画面にフォワードする
		RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/jsp/cheese_edit_music.jsp");
		dispatcher.forward(request, response);
				
				
//		// CheeseEditMusicServlet の doGet 内
//		CheeseMusicPhraseDao phraseDao = new CheeseMusicPhraseDao();
//		List<CheeseMusicPhrase> phraseOptions = phraseDao.findAll(); // ユーザーIDに応じて絞るなら別途条件追加
//		request.setAttribute("phraseOptions", phraseOptions);

	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// もしもログインしていなかったらログインサーブレットにリダイレクトする
		HttpSession session = request.getSession();
		if (session.getAttribute("loginUser") == null) {
			response.sendRedirect(request.getContextPath() + "/CheeseLoginServlet");
			return;
		}
		
		
		// リクエストパラメータを取得する
		request.setCharacterEncoding("UTF-8");
		int musicId = Integer.parseInt(request.getParameter("music_id"));
		String[] strPhraseIdArray = request.getParameterValues("phrase_id");
		int[] phraseIdArray = new int[strPhraseIdArray.length];
		for (int i = 0; i < strPhraseIdArray.length; i++) {
            phraseIdArray[i] = Integer.parseInt(strPhraseIdArray[i]);
            System.out.println(phraseIdArray[i] );
        }
		String[] titleArray = request.getParameterValues("title");
		String[] remarksArray = request.getParameterValues("remarks");
		
		System.out.println(titleArray);
		System.out.println(remarksArray);
		
		// 保存を行う
		CheeseMusicPhraseDao bDao = new CheeseMusicPhraseDao();
		
		if (bDao.save(musicId, phraseIdArray, titleArray, remarksArray)) { // 更新成功
			request.getSession().setAttribute("result", "保存しました。");
		} else { // 更新失敗
			request.getSession().setAttribute("result", "保存に失敗しました。");
		}
	 
		
		// 結果ページにフォワードする
		response.sendRedirect(request.getContextPath() + "/CheeseEditMusicServlet?id=" + musicId);
//		RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/jsp/cheese_edit_music.jsp");
//		dispatcher.forward();
				
	}
}
