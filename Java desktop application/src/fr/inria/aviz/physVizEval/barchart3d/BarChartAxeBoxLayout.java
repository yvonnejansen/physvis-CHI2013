package fr.inria.aviz.physVizEval.barchart3d;

import java.util.ArrayList;

import org.jzy3d.colors.Color;
import org.jzy3d.plot3d.primitives.axes.layout.AxeBoxLayout;
import org.jzy3d.plot3d.primitives.axes.layout.providers.SmartTickProvider;
import org.jzy3d.plot3d.primitives.axes.layout.providers.StaticTickProvider;
import org.jzy3d.plot3d.primitives.axes.layout.renderers.DefaultDecimalTickRenderer;
import org.jzy3d.plot3d.primitives.axes.layout.renderers.IntegerTickRenderer;
import org.jzy3d.plot3d.primitives.axes.layout.renderers.TickLabelMap;

import fr.inria.aviz.physVisEval.data.Tick;

public class BarChartAxeBoxLayout extends AxeBoxLayout {

	public BarChartAxeBoxLayout() {
		super();
        setXAxeLabelDisplayed(false);
        setYAxeLabelDisplayed(false);
        setZAxeLabelDisplayed(false);
        setXTickLabelDisplayed(true);
        setYTickLabelDisplayed(true);
        float[] x = new float[] {0f, 7f, 14f, 21f, 28f, 35f, 42f, 49f, 56f, 63f};
        float[] y = x; //new float[] {-28f, -21f, -14f, -7f, 0f, 7f, 14f, 21f, 28f, 35f};
		setZTickProvider(new SmartTickProvider(9));
		setXTickProvider(new StaticTickProvider(x));
		setYTickProvider(new StaticTickProvider(y));
		setXTickRenderer(new TickLabelMap());
		setYTickRenderer(new TickLabelMap());
		setZTickRenderer(new IntegerTickRenderer());

        
//        setMainColor(Color.MAGENTA);
        
	}
	
	public BarChartAxeBoxLayout(String[] rows, String[] cols, int xDist, int yDist, ArrayList<Tick> zTicks)
	{
		super();
        setXAxeLabelDisplayed(false);
        setYAxeLabelDisplayed(false);
        setZAxeLabelDisplayed(false);
        setXTickLabelDisplayed(true);
        setYTickLabelDisplayed(true);
       
        setQuadColor(new Color(0.1f, 0.1f, 0.1f));
        setXTickColor(new Color(0.3f, 0.3f, 0.3f));
        setYTickColor(new Color(0.3f, 0.3f, 0.3f));
        setZTickColor(new Color(0.3f, 0.3f, 0.3f));
        setGridColor(new Color(0.5f, 0.5f, 0.5f));

        float[] x = new float[rows.length];
        float[] y = new float[cols.length];
        float[] z = new float[zTicks.size()];
        for (int i = 0; i < x.length; i++)
        {
        	x[i] = i * xDist;
        }
        for (int i = 0; i < y.length; i++)
        {
        	y[i] = i * yDist;
        }
        for (int i = 0; i < z.length; i++)
        {
        	z[i] = (float) zTicks.get(i).value;
        }
		setZTickProvider(new StaticTickProvider(z));
		setXTickProvider(new StaticTickProvider(x));
		setYTickProvider(new StaticTickProvider(y));
		setXTickRenderer(new TickLabelMap());
		for (int i = 0; i < x.length; i++)
		{
			((TickLabelMap)xTickRenderer).register(x[i], rows[i]);
		}
		setYTickRenderer(new TickLabelMap());
		for (int i = 0; i < y.length; i++)
		{
			((TickLabelMap)yTickRenderer).register(y[i], cols[i]);
		}
		setZTickRenderer(new TickLabelMap());
		for (int i = 0; i < z.length; i++)
		{
			((TickLabelMap)zTickRenderer).register(z[i], zTicks.get(i).label);
		}

	}
	

	public float[] getZTicks(float min, float max)
	{
		lastYmin = 0;
		lastYmax = max;
		zTicks = zTickProvider.generateTicks(0, max);
		return zTicks;
	}
	
//	public float[] getXTicks(float min, float max)
//	{
//		
//	}
}
