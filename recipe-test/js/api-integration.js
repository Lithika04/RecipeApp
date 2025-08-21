const API_BASE_URL = 'http://localhost:8080/api/recipes';

async function makeAPICall(endpoint, method = 'GET', data = null) {
    const options = {
        method: method,
        headers: {
            'Content-Type': 'application/json',
        }
    };
    
    if (data && method !== 'GET') {
        options.body = JSON.stringify(data);
    }
    
    try {
        const response = await fetch(`${API_BASE_URL}${endpoint}`, options);
        
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        
        return await response.json();
    } catch (error) {
        // Return "failed to load" for any error
        return "failed to load";
    }
}

async function loadAllRecipes() {
    return await makeAPICall('/');
}

async function searchRecipes(query) {
    return await makeAPICall(`/search/${encodeURIComponent(query)}`);
}

async function getRecipesByCuisine(cuisine) {
    return await makeAPICall(`/cuisine/${encodeURIComponent(cuisine)}`);
}

async function getTopRatedRecipes(limit = 6) {
    return await makeAPICall(`/top/${limit}`);
}

window.RecipeAPI = {
    loadAllRecipes,
    searchRecipes,
    getRecipesByCuisine,
    getTopRatedRecipes
};
