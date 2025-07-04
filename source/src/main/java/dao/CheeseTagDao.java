package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import dto.CheeseTag;

public class CheeseTagDao {
	
	public int insert(CheeseTag card) {
		Connection conn = null;
		int id = 0;
	
		try {
			// JDBCドライバを読み込む
			Class.forName("com.mysql.cj.jdbc.Driver");
	
			// データベースに接続する
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/b5?"
					+ "characterEncoding=utf8&useSSL=false&serverTimezone=GMT%2B9&rewriteBatchedStatements=true",
					"root", "password");
			
			// SQL文を準備する
			String sql = "INSERT INTO tags (id, name, user_id) VALUES (0, ?, ?)";
			PreparedStatement pStmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			
			// SQL文を完成させる
			if (card.getName() != null) {
			pStmt.setString(1, card.getName());
			} else {
			pStmt.setString(1, "");
			}
			
			pStmt.setInt(2,  card.getUserId());

			// SQL文を実行する
			if (pStmt.executeUpdate() == 1) {
				// idを取得
				ResultSet rs;
				rs = pStmt.getGeneratedKeys();
				if (rs.next()) {
					id = rs.getInt(1);
				}
			}
				
		} catch (SQLException e) {
				e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
		     // データベースを切断
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		
		// 結果を返すs
		return id;
	}
	
	public boolean delete(int tagId) {
		Connection conn = null;
		boolean result = false;

		try {
			// JDBCドライバを読み込む
			Class.forName("com.mysql.cj.jdbc.Driver");

			// データベースに接続する
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/b5?"
					+ "characterEncoding=utf8&useSSL=false&serverTimezone=GMT%2B9&rewriteBatchedStatements=true",
					"root", "password");

			// SQL文を準備する
			String sql = "DELETE FROM tags WHERE id=?";
			PreparedStatement pStmt = conn.prepareStatement(sql);

			// SQL文を完成させる
			pStmt.setInt(1, tagId);

			// SQL文を実行する
			if (pStmt.executeUpdate() == 1) {
				result = true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			// データベースを切断
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

		// 結果を返す
		return result;
	}
	
	public List<CheeseTag> select(List<Integer> tagIdList) {
		Connection conn = null;
		List<CheeseTag> tagList = new ArrayList<CheeseTag>();
        // tagIdListが空でないことを確認
        if (tagIdList.isEmpty()) {
            return tagList; // 空のリストを返す
        }
		
		try {
			// JDBCドライバを読み込む
			Class.forName("com.mysql.cj.jdbc.Driver");

			// データベースに接続する
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/b5?"
					+ "characterEncoding=utf8&useSSL=false&serverTimezone=GMT%2B9&rewriteBatchedStatements=true",
					"root", "password");

			// SQL文を準備する
			StringBuilder sql = new StringBuilder();
			sql.setLength(0);
			sql.append("SELECT * from tags WHERE");
			for (int i = 0; i < tagIdList.size(); i++) {
				if (i != 0) {
					sql.append(" OR ");
				}
				sql.append(" id = ? ");
			}
			
			PreparedStatement pStmt = conn.prepareStatement(sql.toString());
			for (int i = 0; i < tagIdList.size(); i++) {
				pStmt.setInt(i + 1,  tagIdList.get(i));
			}
			
			// SELECT文を実行し、結果表を取得する
			ResultSet rs = pStmt.executeQuery();
			
			while (rs.next()) {
				CheeseTag tag = new CheeseTag(
						rs.getInt("id"),
						rs.getString("name"),
						rs.getInt("user_id"),
						rs.getString("updated_at"),
						rs.getString("created_at")
						);
				tagList.add(tag);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			tagList = null;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			tagList = null;
		} finally {
			// データベースを切断
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
					tagList = null;
				}
			}
		}

		// 結果を返す
		return tagList;
	}
	
	public List<CheeseTag> selectALL(int userId) {
		Connection conn = null;
		List<CheeseTag> tagList = new ArrayList<CheeseTag>();

		try {
			// JDBCドライバを読み込む
			Class.forName("com.mysql.cj.jdbc.Driver");

			// データベースに接続する
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/b5?"
					+ "characterEncoding=utf8&useSSL=false&serverTimezone=GMT%2B9&rewriteBatchedStatements=true",
					"root", "password");

			// SQL文を準備する
			String sql = "SELECT * FROM tags WHERE user_id=?";
			PreparedStatement pStmt = conn.prepareStatement(sql);
			pStmt.setInt(1,  userId);
			
			// SELECT文を実行し、結果表を取得する
			ResultSet rs = pStmt.executeQuery();
			
			while (rs.next()) {
				CheeseTag tag = new CheeseTag(
						rs.getInt("id"),
						rs.getString("name"),
						rs.getInt("user_id"),
						rs.getString("updated_at"),
						rs.getString("created_at")
						);
				tagList.add(tag);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			tagList = null;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			tagList = null;
		} finally {
			// データベースを切断
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
					tagList = null;
				}
			}
		}

		// 結果を返す
		return tagList;
	}
	

	public CheeseTag findByName(String tagName) {
	    Connection conn = null;
	    CheeseTag cheeseTag = null;

	    try {
	        Class.forName("com.mysql.cj.jdbc.Driver");
	        conn = DriverManager.getConnection(
	            "jdbc:mysql://localhost:3306/b5?characterEncoding=utf8&useSSL=false&serverTimezone=GMT%2B9",
	            "root", "password"
	        );

	      
	        String sql = "SELECT * FROM tags WHERE name = ?";
	        PreparedStatement pStmt = conn.prepareStatement(sql);
	        pStmt.setString(1, tagName);
	        ResultSet rs = pStmt.executeQuery();

	        if (rs.next()) {
	          
	            cheeseTag = new CheeseTag(
	                rs.getInt("id"),
	                rs.getString("name"),
	                rs.getInt("user_id"),
	                rs.getString("updated_at"),
	                rs.getString("created_at")
	            );
	        } 
	        

	    } catch (SQLException | ClassNotFoundException e) {
	        e.printStackTrace();
	    } finally {
	        if (conn != null) {
	            try {
	                conn.close();
	            } catch (SQLException e) {
	                e.printStackTrace();
	            }
	        }
	    }

	    return cheeseTag;
	}
	public CheeseTag insertAndReturn(String tagName, int userId) {
	    Connection conn = null;
	    CheeseTag cheeseTag = null;

	    try {
	        Class.forName("com.mysql.cj.jdbc.Driver");
	        conn = DriverManager.getConnection(
	            "jdbc:mysql://localhost:3306/b5?characterEncoding=utf8&useSSL=false&serverTimezone=GMT%2B9",
	            "root", "password"
	        );

	       
	        String sql = "INSERT INTO tags (name, user_id, created_at, updated_at) VALUES (?, ?, NOW(), NOW())";
	        PreparedStatement pStmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

	        pStmt.setString(1, tagName);
	        pStmt.setInt(2, userId);

	        int affectedRows = pStmt.executeUpdate();

	        if (affectedRows > 0) {
	            try (ResultSet rs = pStmt.getGeneratedKeys()) {
	                if (rs.next()) {
	                    int id = rs.getInt(1);
	                    cheeseTag = new CheeseTag(id, tagName, userId, "", "");
	                }
	            }
	        }

	    } catch (SQLException | ClassNotFoundException e) {
	        e.printStackTrace();
	    } finally {
	        if (conn != null) {
	            try {
	                conn.close();
	            } catch (SQLException e) {
	                e.printStackTrace();
	            }
	        }
	    }

	    return cheeseTag;
	}
	
	
}
		
	


