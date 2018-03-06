package fr.inria.aviz.physVizEval.barchart2d;

import java.awt.*;
import java.awt.geom.*;
import java.util.ArrayList;
import java.util.Hashtable;

import javax.swing.*;
import javax.swing.GroupLayout.Alignment;

import fr.inria.aviz.physVisEval.data.AxisLabeling;
import fr.inria.aviz.physVisEval.data.DataInfo;
import fr.inria.aviz.physVisEval.data.MatrixData;
import fr.inria.aviz.physVisEval.data.Tick;
import fr.inria.aviz.physVizEval.util.GUIUtils;

public class JBarChart2D extends JComponent {

	// -- Graphics constants
	// Layout
	double hmargin_left = 0.20; // horizontal window margin, between 0 and 1
	double hmargin_right = 0.20; // horizontal window margin, between 0 and 1
	double vmargin = 0.07; // vertical window margin, between 0 and 1
	double vspacing = 0.02; // vertical spacing between bars, between 0 and 1
	double hspacing = 0.02; // horizontal spacing between bars, between 0 and 1
	double mindotsize = 3; // in pixels
	double dotSize = 0.1; // between 0 and 1, in proportion of bar width or height  
	// Misc
	static Color bgColor = new Color(0.95f, 0.95f, 0.95f);
	Color chartBgColor = new Color(1f, 1f, 1f);
	Color chartBorderColor = new Color(0.6f, 0.6f, 0.6f);
	Stroke normalStroke = new BasicStroke(1);
	Stroke selectedStroke = new BasicStroke(2);
	Color selectionColor = new Color(0, 0, 0, 0.3f);
	Color selectionBorderColor = new Color(0, 0, 0, 0.7f);
	// Axis
	Color lineAxisColor = new Color(0, 0, 0, 0.3f);
	Color lightLineAxisColor = new Color(0, 0, 0, 0.2f);
	Color barLineAxisColor = new Color(1, 1, 1, 0.5f);
	Color barLightLineAxisColor = new Color(1, 1, 1, 0f);
	Font axisLabelFont = new Font("Helvetica", 0, 18);
	Color axisLabelColor = new Color(0.45f, 0.45f, 0.45f, 1f);
	// Bars
	Color barBorderColor = new Color(0, 0, 0, 0.4f);
	Color selectedBarBorderColor = new Color(0, 0, 0, 1f);
	// Line chart
	Color defaultLineGraphColor = new Color(0.6f, 0.6f, 0.6f);//(0, 0, 0, 0.4f);
	Stroke lineGraphStroke = new BasicStroke(1);
	float unselectedLineGraphBrightness = 0.5f;
	// Text
	Font labelFont = new Font("Helvetica", 0, 18);
	Color rowLabelColor = new Color(0, 0, 0, 1f);
	Color columnLabelColor = new Color(0, 0, 0, 1f);//Color(0.4f, 0.4f, 0.4f, 1f);
	Color labelBgColor = new Color(1, 1, 1, 0.0f);
	Color labelBorderColor = new Color(0, 0, 0, 0.08f);
	Color selectedLabelBgColor = new Color(0, 0, 0, 0.05f);
	Color selectedLabelBorderColor = selectionColor;
	int labelBorderMargin = 5;
	
	// Options
	boolean showTopLabels = true;
	boolean showBottomLabels = true;
	boolean verticalBars = true;
	boolean horizontalBars = false;
	boolean centeredBars = false;
	boolean verticalLines = false;
	boolean horizontalLines = false;
	boolean verticalSuperimpose = false;
	boolean horizontalSuperimpose = false;
	boolean showBackgroundAxisLines = true;
	boolean showAxisLinesOnBars = true;
	Direction showOcclusions = Direction.NONE;

	//
	MatrixData data = null;
	DataInfo axis = null;
	Rectangle2D.Double rec = new Rectangle2D.Double();
	Point2D.Double point1 = new Point2D.Double();
	Point2D.Double point2 = new Point2D.Double();
	Line2D.Double line = new Line2D.Double();
	Ellipse2D.Double ellipse = new Ellipse2D.Double();
	Rectangle[] hChartBounds = null; // for geometrical picking
	Rectangle[] vChartBounds = null; // for geometrical picking
	Rectangle2D.Double[][] barBounds = null;
	int selectedRow = -1;
	int selectedCol = -1;

	public void setData(MatrixData data) {
				
		// Update data
		this.data = data;
		
		// Update charts
		if (data == null) {
			hChartBounds = null;
			vChartBounds = null;
			barBounds = null;
		} else {
			hChartBounds = new Rectangle[data.rows];
			vChartBounds = new Rectangle[data.cols];
			barBounds = new Rectangle2D.Double[data.rows][data.cols];
			//data.unselectAll();
		}
		selectedRow = -1;
		selectedCol = -1;
		repaint();
	}

	public void setSelectedRow(int index) {
		this.selectedRow = index;
		repaint();
	}

	public void setSelectedColumn(int index) {
		this.selectedCol = index;
		repaint();
	}
	
	public void noLabelBorder() {
		labelBgColor = new Color(0, 0, 0, 0f);
		labelBorderColor = new Color(0, 0, 0, 0f);
		selectedLabelBgColor = new Color(0, 0, 0, 0f);
		selectedLabelBorderColor = new Color(0, 0, 0, 0f);;	
	}

	public void paint(Graphics g_) {
		Graphics2D g = (Graphics2D)g_;

		g.setColor(bgColor);
		g.fillRect(0, 0, getWidth(), getHeight());

		if (data == null)
			return;

		int w = getWidth();
		int h = getHeight();
		int nx = horizontalSuperimpose ? 1 : data.cols;
		int ny = verticalSuperimpose ? 1 : data.rows;

		if (nx == 0 || ny == 0)
			return;
		
		// Spacing is specified for a 10x10 matrix. For larger datasets we
		// have to reduce it, although maybe not linearly.
		double hSpacingFactor = Math.pow(10.0 / Math.max(10, nx), 1);
		double vSpacingFactor = Math.pow(10.0 / Math.max(10, ny), 1);

		int marginx_left = (int)(w * hmargin_left);
		int marginx_right = (int)(w * hmargin_right);
		int marginy = (int)(h * vmargin);
		int wused = w - (marginx_left + marginx_right);
		int hused = h - marginy * 2;
		int hspace = (int)(wused * hspacing * hSpacingFactor);
		int cellwidth = (wused - (nx - 1) * hspace) / nx;
		int vspace = (int)(hused * vspacing * vSpacingFactor);
		int cellheight = (hused - (ny - 1) * vspace) / ny;
		// correct for rounding errors
		int new_wused = nx * cellwidth + (nx-1) * hspace;
		marginx_left = marginx_left * wused / new_wused;
		marginx_right = w - new_wused - marginx_left;
		wused = new_wused;
		hused = ny * cellheight + (ny-1) * vspace;
		marginy = (h - hused) / 2;
		AxisLabeling axisInfo = data.getDataInfo().getAxisLabeling();
		
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		//////////////////////////////////////////////////////////////////////////////////////

		int x0, x1, y0, y1;
		double ah;

		// -- Column labels and vertical bar chart backgrounds
		
		// first, determine if column and axis labels should be vertical or horizontal
		boolean vertical = false;
		boolean ticksVertical = false;
		g.setFont(labelFont);
		for (int i = 0; i < data.cols; i++) {
			GUIUtils.CachedMetrics m = GUIUtils.getCachedMetrics(g, data.getColumnLabel(i));
			vertical = vertical || (m.bounds.getHeight() < m.bounds.getWidth() && m.bounds.getWidth() > (hspace + cellwidth) - 4);
		}
		g.setFont(axisLabelFont);
		
		double axisMax = Math.max(axisInfo.getMaxAxisValue(), data.computeMax());
		double axisMin = Math.min(axisInfo.getMinAxisValue(), data.computeMin());
		double axisRange = axisMax - axisMin;
		axisInfo.setNewMaxAxisValue(axisMax);
		
		double availwidth = cellwidth * axisInfo.getFirstTickStep() / axisRange;
		for (Tick tick : axisInfo.ticks) {
			GUIUtils.CachedMetrics m = GUIUtils.getCachedMetrics(g, tick.label);
			ticksVertical = ticksVertical || (m.bounds.getHeight() < m.bounds.getWidth() && m.bounds.getWidth() > availwidth * 0.8);
		}
		
		y0 = marginy;
		y1 = h - marginy;

		for (int i = 0; i < data.cols; i++) {
			
			int xstep = horizontalSuperimpose ? 0 : cellwidth + hspace;
			x0 = marginx_left + i * xstep;
			x1 = x0 + cellwidth;

			Rectangle vchartbounds = new Rectangle(x0, marginy, x1 - x0, hused);

			if (i == 0 || !horizontalSuperimpose) {
			
				// -- Vertical bar chart backgrounds
				if (vchartbounds.width > 0 && vchartbounds.height > 0) {
					if ((horizontalBars && !verticalBars) || (!horizontalLines && verticalLines)) {
						vchartbounds.setBounds(vchartbounds.x, vchartbounds.y - vspace/2, vchartbounds.width, vchartbounds.height + vspace);
						g.setColor(chartBgColor);
						g.fill(vchartbounds);
						g.setStroke(normalStroke);
						g.setColor(chartBorderColor);
						g.draw(vchartbounds);
					}
				}
	
				// -- Vertical axis lines
				if ((horizontalBars && !verticalBars && !centeredBars) || (verticalLines && !horizontalLines)) {
					g.setStroke(normalStroke);
					ah = (x1 - x0) * axisInfo.getFirstTickStep() / axisRange;
					boolean lightLines = (ah < 20) || (horizontalBars && showAxisLinesOnBars); 
					for (Tick tick : axisInfo.ticks) {
						double x = x0 + (x1 - x0) * axisInfo.getPositionOnAxis(tick.value);
						g.setColor(lightLines ? lightLineAxisColor : lineAxisColor);
						line.setLine(x, marginy - vspace/2, x, h - marginy + hspace/2);
						g.draw(line);
						// label
						if (ah > 20) {
							g.setFont(axisLabelFont);
							g.setColor(axisLabelColor);
							double ly = h - marginy + vspace;
							if (!ticksVertical)
								GUIUtils.drawText(g, tick.label, x, ly, ah * 3 / 4, h - ly - vspace, GUIUtils.HALIGN.Center, GUIUtils.VALIGN.Top, 0);
							else
								GUIUtils.drawText(g, tick.label, x, ly, h - ly - vspace, ah * 3 / 4, GUIUtils.HALIGN.Right, GUIUtils.VALIGN.Middle, -Math.PI/2);
							line.setLine(x, h - marginy + vspace/2 + 1, x, h - marginy + vspace * 3 / 4);
							g.draw(line);
						}
					}
				}
				
			}
			vchartbounds.setBounds(vchartbounds.x - hspace/2, vchartbounds.y - vspace/2, vchartbounds.width + hspace, vchartbounds.height + vspace);
			vChartBounds[i] = vchartbounds;

			// -- Column label
			String s = data.getColumnLabel(i);
			g.setColor(columnLabelColor);
			g.setFont(labelFont);
			if (!vertical) {
				// Draw labels horizontally
				if (showTopLabels)
					GUIUtils.drawText(g, s, (x0 + x1) / 2, y0 - vspace - 2, (hspace + cellwidth) - 4, marginy - vspace/2 - 4, GUIUtils.HALIGN.Center, GUIUtils.VALIGN.Bottom, 0,
							selectedCol == i ? selectedLabelBgColor : labelBgColor, selectedCol == i ? selectedLabelBorderColor : labelBorderColor, labelBorderMargin);
				if (showBottomLabels)
					GUIUtils.drawText(g, s, (x0 + x1) / 2, y1 + vspace + 2, (hspace + cellwidth) - 4, marginy - vspace/2 - 4, GUIUtils.HALIGN.Center, GUIUtils.VALIGN.Top, 0,
							selectedCol == i ? selectedLabelBgColor : labelBgColor, selectedCol == i ? selectedLabelBorderColor : labelBorderColor, labelBorderMargin);
			} else {
				// Draw labels vertically
				if (showTopLabels) {
					GUIUtils.drawText(g, s, (x0 + x1) / 2, y0 - vspace - 2, marginy - vspace - 6, (hspace + cellwidth) - 4, GUIUtils.HALIGN.Left, GUIUtils.VALIGN.Middle, -Math.PI/2,
							selectedCol == i ? selectedLabelBgColor : labelBgColor, selectedCol == i ? selectedLabelBorderColor : labelBorderColor, labelBorderMargin);
				}
				if (showBottomLabels) {
					GUIUtils.drawText(g, s, (x0 + x1) / 2, y1 + vspace + 2, marginy - vspace - 6, (hspace + cellwidth) - 4, GUIUtils.HALIGN.Right, GUIUtils.VALIGN.Middle, -Math.PI/2,
							selectedCol == i ? selectedLabelBgColor : labelBgColor, selectedCol == i ? selectedLabelBorderColor : labelBorderColor, labelBorderMargin);
				}
			}
		}

		// -- Now go through the whole matrix

		for (int j = 0; j < data.rows; j++) {
			
			
//System.err.println("ROW " + j + " " + data.getRowLabel(j));
			int ystep = verticalSuperimpose ? 0 : cellheight + vspace;
			y0 = marginy + j * ystep;
			y1 = y0 + cellheight;
			
			Rectangle hchartbounds = new Rectangle(marginx_left, y0, wused, y1 - y0);

			if (j == 0 || !verticalSuperimpose) {

				// -- Horizontal bar chart backgrounds
				if (hchartbounds.width > 0 && hchartbounds.height > 0) {
					if ((!horizontalBars && verticalBars) || (horizontalLines && !verticalLines)) {
						hchartbounds.setBounds(hchartbounds.x - hspace/2, hchartbounds.y, hchartbounds.width + hspace, hchartbounds.height);
						g.setColor(chartBgColor);
						g.fill(hchartbounds);
						g.setStroke(normalStroke);
						g.setColor(chartBorderColor);
						g.draw(hchartbounds);
					} 
				}
	
				// -- Horizontal axis lines
				if ((!horizontalBars && verticalBars && !centeredBars) || (horizontalLines && !verticalLines)) {
					g.setStroke(normalStroke);
					ah = (y1 - y0) * axisInfo.getFirstTickStep() / axisRange;
					boolean lightLines = (ah < 20) || (verticalBars && showAxisLinesOnBars); 
					for (Tick tick : axisInfo.ticks) {
						double y = y1 - (y1 - y0) * axisInfo.getPositionOnAxis(tick.value);
						// background axis lines
						if (showBackgroundAxisLines) {
							g.setColor(lightLines ? lightLineAxisColor : lineAxisColor);
							line.setLine(marginx_left - hspace/2, y, w - marginx_right + hspace/2, y);
							g.draw(line);
						}
						// label
						if (ah > 20) {
							g.setFont(axisLabelFont);
							g.setColor(axisLabelColor);
							double lx = w - marginx_right + hspace;
							GUIUtils.drawText(g, tick.label, lx, y, w - lx - hspace, ah * 3 / 4, GUIUtils.HALIGN.Left, GUIUtils.VALIGN.Middle, 0);
							line.setLine(w - marginx_right + hspace/2 + 1, y, w - marginx_right + hspace * 3 / 4, y);
							g.draw(line);
						}
					}
				}
				
			}
			hchartbounds.setBounds(hchartbounds.x - hspace/2, hchartbounds.y - vspace/2, hchartbounds.width + hspace, hchartbounds.height + vspace);
			hChartBounds[j] = hchartbounds;
			
			// -- Row labels
			String s = data.getRowLabel(j);
			g.setFont(labelFont);
			g.setColor(rowLabelColor);
			if (!verticalSuperimpose) {
				GUIUtils.drawText(g, s, marginx_left - hspace - 2, (y1 + y0) / 2, marginx_left - hspace - 4, cellheight, GUIUtils.HALIGN.Right, GUIUtils.VALIGN.Middle, 0,
						selectedRow == j ? selectedLabelBgColor : labelBgColor, selectedRow == j ? selectedLabelBorderColor : labelBorderColor, labelBorderMargin);
			} else {
				// if superimposed, use a legend instead
				int lh = (int)GUIUtils.getCachedMetrics(g, s).bounds.getHeight();
				int sh = lh * 2 / 3;
				int legendHeight = data.rows * lh + (data.rows - 1) * sh;
				int y = marginy + j * (lh + sh) + (hused - legendHeight) / 2;
				int x = Math.max(lh * 2, marginx_left - wused / 4);
				GUIUtils.drawText(g, s, x, y, marginx_left - x - lh, hused / data.rows, GUIUtils.HALIGN.Left, GUIUtils.VALIGN.Middle, 0,
						selectedRow == j ? selectedLabelBgColor : labelBgColor, selectedRow == j ? selectedLabelBorderColor : labelBorderColor, labelBorderMargin);
				if (data.areAllRowColorsEqual(j)) {
					g.setColor(data.getColor(j, 0));
					g.fillRect(x - lh - 2, y - lh/2 + lh/4, lh - lh/3, lh - lh/3);
				}
				hchartbounds.y = y - sh / 2;
				hchartbounds.height = lh + sh;
				hChartBounds[j] = hchartbounds;
			}
				
			// -- Bars
			g.setStroke(normalStroke);
			for (int i = 0; i < data.cols; i++) {
				
				int xstep = horizontalSuperimpose ? 0 : cellwidth + hspace;
				x0 = marginx_left + i * xstep;
				x1 = x0 + cellwidth;

				// Cell background
				if (horizontalBars && verticalBars) {
					rec.setRect(x0, y0, x1- x0, y1 - y0);
					g.setColor(chartBgColor);
					g.fill(rec);
					//if (selectedCol != i || selectedRow != j) {
						g.setStroke(normalStroke);
						g.setColor(chartBorderColor);
						g.draw(rec);
//					} else {
//						g.setStroke(selectedStroke);
//						g.setColor(Color.black);
//						int r = 1;
//						g.drawRect(hchartbounds.x - r, hchartbounds.y - r, hchartbounds.width + r, hchartbounds.height + r);
//					}
				}

				// Occlusion bar
				if ((horizontalBars || verticalBars) && showOcclusions != Direction.NONE) {
					double occl = computeOcclusion(j, i, showOcclusions);
					computeBarBounds(rec, x0, y0, x1, y1, data.getValue(j, i) + occl);
					g.setColor(new Color(0, 0, 0, 0.3f));
					g.fill(rec);
				}
				
				// Bar
				if (horizontalBars || verticalBars) {
					computeBarBounds(rec, x0, y0, x1, y1, data.getValue(j, i));
					if (barBounds != null) {
						Rectangle2D.Double rec2 = (Rectangle2D.Double)rec.clone();
//						System.err.println(j + " " + i);
//						System.err.println(barBounds.length + " " + i);
						barBounds[j][i] = rec2; 
					}

					// Bar fill
					g.setColor(data.getColor(j,i));
					g.fill(rec);
	
					// Bar border
					rec.width --;
					g.setStroke(normalStroke);
					if (!data.isSelected(j, i)) {//(j != selectedRow && i != selectedCol) {
						g.setStroke(normalStroke);
						g.setColor(barBorderColor);
						g.draw(rec);
					} else {
						g.setStroke(selectedStroke);
						g.setColor(selectedBarBorderColor);
						g.draw(rec);
					}
					g.setStroke(normalStroke);
				}
				
				// Line
				if (horizontalLines || verticalLines) {
					computeLineVertex(point1, x0, y0, x1, y1, data.getValue(j, i));
					g.setStroke(lineGraphStroke);
					// Horizontal
					if (horizontalLines && i > 0) {
						float brightness = 0;
						if (selectedRow != -1 && j != selectedRow)
							brightness = unselectedLineGraphBrightness;
						computeLineVertex(point2, x0 - cellwidth - hspace, y0, x1 - cellwidth - hspace, y1, data.getValue(j, i - 1));
						line.setLine(point1, point2);
						if (data.getColor(j,i).equals(data.getColor(j, i - 1)))
							g.setColor(GUIUtils.mix(data.getColor(j, i), Color.white, brightness));
						else
							g.setColor(GUIUtils.mix(defaultLineGraphColor, Color.white, brightness));
						g.draw(line);
					}
					// Vertical
					if (verticalLines && j > 0) {
						float brightness = 0;
						if (selectedCol != -1 && j != selectedCol)
							brightness = unselectedLineGraphBrightness;
						computeLineVertex(point2, x0, y0 - cellheight - vspace, x1, y1 - cellheight - vspace, data.getValue(j - 1, i));
						line.setLine(point1, point2);
						if (data.getColor(j,i).equals(data.getColor(j - 1, i)))
							g.setColor(GUIUtils.mix(data.getColor(j, i), Color.white, brightness));
						else
							g.setColor(GUIUtils.mix(defaultLineGraphColor, Color.white, brightness));
						g.draw(line);
					}
				}
				
				// -- Axis lines on bar
				if (showAxisLinesOnBars) {
					g.setStroke(normalStroke);
					for (Tick tick : axisInfo.ticks) {
						if (tick.value < data.getValue(j, i)) {
							double x = x0 + (x1 - x0) * axisInfo.getPositionOnAxis(tick.value);
							double y = y1 - (y1 - y0) * axisInfo.getPositionOnAxis(tick.value);
							// horizontal axis lines
							if (!horizontalBars && verticalBars && !centeredBars) {
								ah = (y1 - y0) * axisInfo.getFirstTickStep() / axisRange;
								g.setColor(ah < 20 ? barLightLineAxisColor : barLineAxisColor);
								line.setLine(x0, y, x1, y);
								g.draw(line);
							}
							// horizontal axis lines
							if (horizontalBars && !verticalBars && !centeredBars) {
								ah = (x1 - x0) * axisInfo.getFirstTickStep() / axisRange;
								g.setColor(ah < 20 ? barLightLineAxisColor : barLineAxisColor);
								line.setLine(x, y0, x, y1);
								g.draw(line);
							}
						}
					}
				}
			}
		}
		
		// Line graph points (must be displayed last)
		
		if (horizontalLines || verticalLines) {
			for (int selected = 0; selected <= 1; selected++) {
				double dotsize = mindotsize + Math.min(cellwidth, cellheight) * dotSize;
				for (int j = 0; j < data.rows; j++) {
					int ystep = verticalSuperimpose ? 0 : cellheight + vspace;
					y0 = marginy + j * ystep;
					y1 = y0 + cellheight;
					for (int i = 0; i < data.cols; i++) {
						if (selectedCol == i || selectedRow == j) {
							if (selected == 0)
								continue;
						} else {
							if (selected == 1)
								continue;
						}
						int xstep = horizontalSuperimpose ? 0 : cellwidth + hspace;
						x0 = marginx_left + i * xstep;
						x1 = x0 + cellwidth;
						computeLineVertex(point1, x0, y0, x1, y1, data.getValue(j, i));
						ellipse.setFrame(point1.x - dotsize/2, point1.y - dotsize/2, dotsize, dotsize);
						float brightness = 0;
						if (selectedRow != -1 && j != selectedRow)
							brightness = unselectedLineGraphBrightness;
						g.setColor(GUIUtils.mix(data.getColor(j, i), Color.white, brightness));
						g.fill(ellipse);
						g.setStroke(normalStroke);
						g.setColor(GUIUtils.multiplyAlpha(barBorderColor, 1 - brightness));
						g.draw(ellipse);
						// selection
						if (selectedCol == i || selectedRow == j) {
	//						g.setStroke(selectedStroke);
							g.setColor(selectedBarBorderColor);
							g.draw(ellipse);
						}
					}
				}
			}
		}
		
		// Row selection border
		for (int j = 0; j < data.rows; j++) {
			if (selectedRow == j && !verticalSuperimpose) {
				g.setStroke(selectedStroke);
				g.setColor(selectionBorderColor);
				g.draw(hChartBounds[j]);
			}
		}
		
		// Column selection border
		for (int i = 0; i < data.cols; i++) {
			if (selectedCol == i && !horizontalSuperimpose) {
				g.setStroke(selectedStroke);
				g.setColor(selectionBorderColor);
				g.draw(vChartBounds[i]);
			}
		}
	}
	
	private void computeBarBounds(Rectangle2D.Double rec, int x0, int y0, int x1, int y1, double value) {
		AxisLabeling axisInfo = data.getDataInfo().getAxisLabeling();
		double bh = (y1 - y0) * axisInfo.getPositionOnAxis(value);
		double bw = (x1 - x0) * axisInfo.getPositionOnAxis(value);

		if (verticalBars && !horizontalBars)
			rec.setRect(x0, y1 - bh, x1 - x0, bh);
		else if (horizontalBars && !verticalBars)
			rec.setRect(x0, y0, bw, y1 - y0);
		else if (horizontalBars && verticalBars)
			rec.setRect(x0, y1 - bh, bw + 0.5, bh);
		else
			rec.setRect((x0 + x1) / 2, (y0 + y1) / 2, 0, 0);
		if (centeredBars)
			rec.setRect(x0 + (x1 - x0 - rec.width) / 2 + 0.5, y0 + (y1 - y0 - rec.height) / 2, rec.width, rec.height);
	}
	
	private void computeLineVertex(Point2D.Double point, int x0, int y0, int x1, int y1, double value) {
		AxisLabeling axisInfo = data.getDataInfo().getAxisLabeling();
		double bh = (y1 - y0) * axisInfo.getPositionOnAxis(value);
		double bw = (x1 - x0) * axisInfo.getPositionOnAxis(value);

		if (horizontalLines && !verticalLines)
			point.setLocation((x0 + x1)/2, y1 - bh);
		else if (verticalLines && !horizontalLines)
			point.setLocation(x0 + bw, (y0 + y1) / 2);
		else if (verticalLines && horizontalLines)
			point.setLocation(x0 + bw, y1 - bh);
		else
			point.setLocation((x0 + x1) / 2, (y0 + y1) / 2);
	}

	public int pickRow(int x, int y, boolean labels, boolean content) {
		int res = -1;
		if (labels)
			res = pickRowLabel(x, y);
		if (res == -1 && content)
			res = pickRowContent(x, y);
		return res;
	}
	
	public int pickColumn(int x, int y, boolean labels, boolean content) {
		int res = -1;
		if (labels)
			res = pickColumnLabel(x, y);
		if (res == -1 && content)
			res = pickColumnContent(x, y);
		return res;
	}

	public int pickRowContent(int x, int y) {
		if (hChartBounds == null)
			return -1;
		for (int i=0; i<hChartBounds.length; i++)
			if (hChartBounds[i].contains(x, y))
				return i;
		return -1;
	}
	
	public int pickColumnContent(int x, int y) {
		if (vChartBounds == null)
			return -1;
		for (int i=0; i<vChartBounds.length; i++)
			if (vChartBounds[i].contains(x, y))
				return i;
		return -1;
	}

	public int pickRowLabel(int x, int y) {
		if (hChartBounds == null)
			return -1;
		for (int i=0; i<hChartBounds.length; i++)
			if (!hChartBounds[i].contains(x, y) && y >= hChartBounds[i].y && y <= hChartBounds[i].y + hChartBounds[i].height)
				return i;
		return -1;
	}
	
	public int pickColumnLabel(int x, int y) {
		if (vChartBounds == null)
			return -1;
		for (int i=0; i<vChartBounds.length; i++)
			if (!vChartBounds[i].contains(x, y) && x >= vChartBounds[i].x && x <= vChartBounds[i].x + vChartBounds[i].width)
				return i;
		return -1;
	}
	
	public int[] pickBar(int x, int y) {
		if (barBounds == null)
			return null;
		for (int r=0; r<data.rows; r++) {
			for (int c=0; c<data.cols; c++) {
				if (barBounds[r][c].contains(x, y))
					return new int[] {r,c};
			}
		}
		return null;
	}
	
    ///////////////////////////////////////////////////////////////////////////////////
	
    public enum Direction {LEFT, RIGHT, TOP, BOTTOM, NONE};
    
    public double computeOcclusionArea(Direction direction) {
    	if (data == null)
    		return 0;

    	double totalOcclusion = 0;
    	double totalArea = data.computeMax() * data.cols * data.rows;
		for (int c=0; c<data.cols; c++) {
			for (int r=0; r<data.rows; r++) {
				totalArea += data.getValue(r, c);
    	    	totalOcclusion = computeOcclusion(r, c, direction);
			}
    	}
		return totalOcclusion / totalArea;
    }
    
    public double computeOcclusionRatio(Direction direction) {
    	if (data == null)
    		return 0;
    	double totalOcclusionRatio = 0;
    	double maxValue = data.computeMax();
		for (int c=0; c<data.cols; c++) {
			for (int r=0; r<data.rows; r++) {
				double value = data.getValue(r, c);
    	    	double occlusion = computeOcclusion(r, c, direction);
    	    	if (value != 0)
    	    		totalOcclusionRatio += occlusion / maxValue;
			}
    	}
		return totalOcclusionRatio / (data.cols * data.rows);
    }

	protected double computeOcclusion(int r, int c, Direction direction) {
    	if (data == null)
    		return 0;

		double datamax = data.computeMax(); 
		
		double value = data.getValue(r, c);
		double maxOcclValue = 0;
		if (direction == Direction.TOP) {
    		for (int r2=r+1; r2<data.rows; r2++) {
    			double h = getValueCorrectedForDistance(r2, c, r, c, datamax);
    			if (h > maxOcclValue)
    				maxOcclValue = h;
    		}
    	} else if (direction == Direction.BOTTOM) {
    		for (int r2=r-1; r2>=0; r2--) {
    			double h = getValueCorrectedForDistance(r2, c, r, c, datamax);
    			if (h > maxOcclValue)
    				maxOcclValue = h;
    		}
    	} else if (direction == Direction.LEFT) {
    		for (int c2=c+1; c2<data.cols; c2++) {
    			double h = getValueCorrectedForDistance(r, c2, r, c, datamax);
    			if (h > maxOcclValue)
    				maxOcclValue = data.getValue(r, c2);
    		}
    	} else if (direction == Direction.RIGHT) {
    		for (int c2=c-1; c2>=0; c2--){
    			double h = getValueCorrectedForDistance(r, c2, r, c, datamax);
    			if (h > maxOcclValue)
    				maxOcclValue = h;
    		}
    	}
		//return Math.min(value, maxOcclValue);
		
		double occlusion = Math.max(0, maxOcclValue - value);
		
		return occlusion;
	}
	
	protected double getValueCorrectedForDistance(int r, int c, int r0, int c0, double datamax) {
		double hBarSpacingInDataUnits = datamax / data.cols; 
		double vBarSpacingInDataUnits = datamax / data.rows; 
		// assumes a 45 degrees viewing angle
		double dx = (r - r0) * vBarSpacingInDataUnits;
		double dy = (c - c0) * hBarSpacingInDataUnits;
		double d = Math.sqrt(dx * dx + dy * dy);
		return Math.max(0, data.getValue(r, c) - d);
	}
}

