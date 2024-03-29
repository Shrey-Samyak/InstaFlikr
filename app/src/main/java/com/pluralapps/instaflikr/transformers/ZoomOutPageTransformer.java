package com.pluralapps.instaflikr.transformers;



import android.support.v4.view.ViewPager.PageTransformer;
import android.view.View;


public class ZoomOutPageTransformer implements PageTransformer {

    private static final float MIN_SCALE = 0.85f;
    private static final float MIN_ALPHA = 0.5f;


    /**
     * O parametro "position" indica onde e que uma pagina esta relativamente ao
     * centro do ecra.
     *
     * Quando uma pagina preenche o ecra o valor de "position" e 0
     * Quando uma pagina esta completamente no lado direito do ecra o valor de "position" e 1
     */
    @Override
    public void transformPage(View view, float position) {
        int pageWidth = view.getWidth();
        int pageHeight = view.getHeight();


        /**
         * Se o valor for menor do que -1 entao quer dizer que a pagina
         * esta fora do ecra no lado esquerdo
         */
        if(position < -1)
            view.setAlpha(0f);
        else if(position <= 1) {
            float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));
            float vertMargin = pageHeight * (1 - scaleFactor) / 2;
            float horzMargin = pageWidth * (1 - scaleFactor) / 2;

            if(position < 0)
                view.setTranslationX(horzMargin - vertMargin / 2);
            else
                view.setTranslationX(-horzMargin + vertMargin / 2);


            view.setScaleX(scaleFactor);
            view.setScaleY(scaleFactor);

            view.setAlpha(MIN_ALPHA + (scaleFactor - MIN_SCALE) / (1 - MIN_SCALE) * (1 - MIN_ALPHA));
        } else {
            //A pagina esta no lado direito do ecra
            view.setAlpha(0f);
        }
    }
}
