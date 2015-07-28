package revolverwheel.revolvercategories;

import com.example.ozzca_000.myapplication.R;

public enum FoodCategory{
    Indian(R.drawable.indian),
    American(R.drawable.american),
    Chinese(R.drawable.chinese),
    Italian(R.drawable.italian),
    Japanese(R.drawable.japanese),
    Mexican(R.drawable.mexican),
    None(-1);

    private int _drawableResource;

    FoodCategory(int drawableResource)
    {
        _drawableResource = drawableResource;
    }

    public int getDrawableResource()
    {
        return _drawableResource;
    }

}
