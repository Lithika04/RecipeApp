package api;

import com.google.gson.Gson;
import database.RecipeMongoDB;
import org.bson.Document;
import org.bson.types.ObjectId;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/api/recipes/*")
public class RecipeAPI extends HttpServlet {
    private Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Access-Control-Allow-Origin", "*");
        
        PrintWriter out = response.getWriter();
        
        try {
            String pathInfo = request.getPathInfo();
            
            if (pathInfo == null || pathInfo.equals("/")) {
                // Get all recipes
                List<Document> recipes = RecipeMongoDB.getAllRecipes();
                out.print(gson.toJson(recipes));
            } else if (pathInfo.startsWith("/cuisine/")) {
                // Get recipes by cuisine
                String cuisine = pathInfo.substring("/cuisine/".length());
                List<Document> recipes = RecipeMongoDB.getRecipesByCuisine(cuisine);
                out.print(gson.toJson(recipes));
            } else if (pathInfo.startsWith("/top/")) {
                // Get top rated recipes
                int limit = Integer.parseInt(pathInfo.substring("/top/".length()));
                List<Document> recipes = RecipeMongoDB.getTopRatedRecipes(limit);
                out.print(gson.toJson(recipes));
            } else if (pathInfo.startsWith("/search/")) {
                // Search recipes by name
                String query = pathInfo.substring("/search/".length());
                List<Document> recipes = RecipeMongoDB.searchRecipes(query);
                out.print(gson.toJson(recipes));
            } else {
                // Get recipe by ID
                String recipeId = pathInfo.substring(1);
                Document recipe = RecipeMongoDB.getRecipeById(recipeId);
                if (recipe != null) {
                    out.print(gson.toJson(recipe));
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    out.print("{\"error\": \"Recipe not found\"}");
                }
            }
            
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Access-Control-Allow-Origin", "*");
        
        PrintWriter out = response.getWriter();
        
        try {
            Recipe recipe = gson.fromJson(request.getReader(), Recipe.class);
            RecipeMongoDB.insertRecipe(
                recipe.getName(),
                recipe.getCuisine(),
                recipe.getRating(),
                recipe.getTotalTime(),
                recipe.getServings(),
                recipe.getDescription()
            );
            
            out.print("{\"message\": \"Recipe created successfully\"}");
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Access-Control-Allow-Origin", "*");
        
        PrintWriter out = response.getWriter();
        
        try {
            String pathInfo = request.getPathInfo();
            if (pathInfo != null && pathInfo.length() > 1) {
                String recipeId = pathInfo.substring(1);
                Recipe recipe = gson.fromJson(request.getReader(), Recipe.class);
                
                RecipeMongoDB.updateRecipe(recipeId, recipe);
                out.print("{\"message\": \"Recipe updated successfully\"}");
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Access-Control-Allow-Origin", "*");
        
        PrintWriter out = response.getWriter();
        
        try {
            String pathInfo = request.getPathInfo();
            if (pathInfo != null && pathInfo.length() > 1) {
                String recipeId = pathInfo.substring(1);
                RecipeMongoDB.deleteRecipe(recipeId);
                out.print("{\"message\": \"Recipe deleted successfully\"}");
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    // Recipe model class
    public static class Recipe {
        private String name;
        private String cuisine;
        private double rating;
        private int totalTime;
        private int servings;
        private String description;

        // Getters and setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getCuisine() { return cuisine; }
        public void setCuisine(String cuisine) { this.cuisine = cuisine; }
        
        public double getRating() { return rating; }
        public void setRating(double rating) { this.rating = rating; }
        
        public int getTotalTime() { return totalTime; }
        public void setTotalTime(int totalTime) { this.totalTime = totalTime; }
        
        public int getServings() { return servings; }
        public void setServings(int servings) { this.servings = servings; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }
}
