package edu.etf.bg.ac.rs.idealgaslawsimulation.elements;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;

import edu.etf.bg.ac.rs.idealgaslawsimulation.geometry.Line2D;
import edu.etf.bg.ac.rs.idealgaslawsimulation.geometry.Particle2D;
import edu.etf.bg.ac.rs.idealgaslawsimulation.geometry.PhObject;
import edu.etf.bg.ac.rs.idealgaslawsimulation.geometry.Point2D;

public class SimulationView extends View {
    private final Paint pLine2D = new Paint(),
            pParticle2D_1 = new Paint(),
            pParticle2D_2 = new Paint();
    private Context context;
    private int w, h;
    private Float minx = null, maxx = null, miny = null, maxy = null;
    private float px_per_m;
    private PhObject[] objects = null;

    public SimulationView(Context context) { this(context, null); }

    public SimulationView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;

        pLine2D.setAntiAlias(true);
        pLine2D.setColor(Color.BLUE);
        pLine2D.setStyle(Paint.Style.STROKE);

        pParticle2D_1.setAntiAlias(true);
        pParticle2D_1.setColor(Color.RED);
        pParticle2D_1.setStyle(Paint.Style.FILL_AND_STROKE);

        pParticle2D_2.setAntiAlias(true);
        pParticle2D_2.setColor(Color.CYAN);
        pParticle2D_2.setStyle(Paint.Style.FILL_AND_STROKE);
    }

    public void setObjects(PhObject[] objects) {
        this.objects = objects;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawColor(Color.WHITE);
        if(objects != null) {
            if(minx == null || maxx == null || miny == null || maxy == null)
            {
                for(PhObject phObject: objects) {
                    if(phObject instanceof Line2D) {
                        Line2D line2D = (Line2D) phObject;
                        updateBounds(line2D.getP1());
                        updateBounds(line2D.getP2());
                    } else if(phObject instanceof Particle2D) {
                        Particle2D particle2D = (Particle2D) phObject;
                        double r = particle2D.getRadius();
                        updateBounds(particle2D.getCenter(), -r, 0);
                        updateBounds(particle2D.getCenter(), r, 0);
                        updateBounds(particle2D.getCenter(), 0, -r);
                        updateBounds(particle2D.getCenter(), 0, r);
                    }
                }
                px_per_m = Math.min(w / (maxx - minx), h / (maxy - miny));
            }

            for(PhObject phObject: objects) {
                if(phObject instanceof Line2D) {
                    Line2D line2D = (Line2D) phObject;
                    canvas.drawLine((float)(line2D.getP1().getX() - minx) * px_per_m, (float)(line2D.getP1().getY() - miny) * px_per_m, (float)(line2D.getP2().getX() - minx) * px_per_m, (float)(line2D.getP2().getY() - miny) * px_per_m, pLine2D);
                } else if(phObject instanceof Particle2D) {
                    Particle2D particle2D = (Particle2D) phObject;
                    canvas.drawCircle((float)(particle2D.getCenter().getX() - minx) * px_per_m, (float)(particle2D.getCenter().getY() - miny) * px_per_m, (float)particle2D.getRadius() * px_per_m, particle2D.getId() == 0 ? pParticle2D_1 : pParticle2D_2);
                }
            }
        }
    }

    private void updateBounds(float x, float y) {
        minx = minx == null ? x : Math.min(minx, x);
        maxx = maxx == null ? x : Math.max(maxx, x);
        miny = miny == null ? y : Math.min(miny, y);
        maxy = maxy == null ? y : Math.max(maxy, y);
    }

    private void updateBounds(Point2D point2D, double offsetX, double offsetY) {
        updateBounds((float)(point2D.getX() + offsetX), (float)(point2D.getY() + offsetY));
    }

    private void updateBounds(Point2D point2D) { updateBounds(point2D, 0, 0); }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        w = MeasureSpec.getSize(widthMeasureSpec);
        h = MeasureSpec.getSize(heightMeasureSpec);

        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
    }
}
