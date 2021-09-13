package ovgu.creasy.origami.oripa.ui;

import javafx.scene.Cursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.transform.Affine;
import oripa.domain.fold.FoldedModel;
import oripa.domain.fold.FolderTool;
import oripa.domain.fold.OverlapRelationList;
import oripa.domain.fold.halfedge.OriFace;
import oripa.domain.fold.halfedge.OrigamiModel;
import oripa.domain.fold.halfedge.TriangleFace;
import oripa.domain.fold.halfedge.TriangleVertex;
import oripa.domain.fold.origeom.OverlapRelationValues;
import oripa.geom.RectangleDomain;
import ovgu.creasy.geom.Point;

import javax.vecmath.Vector2d;
import java.util.List;

/**
 * A screen to show the folded state of origami.
 *
 * @author Koji, xkevio
 *
 */
public class FoldedModelScreen extends Canvas {

    // private Canvas bufferImage;
    private GraphicsContext bufferGraphicsContext;

    private final int[] pbuf; // 32bit pixel buffer
    private final int[] zbuf; // 32bit z buffer
    private final int BUFFERW; // width
    private final int BUFFERH; // height
    private final int[] min;
    private final int[] max;
    private final int[] minr;
    private final int[] maxr;
    private final int[] ming;
    private final int[] maxg;
    private final int[] minb;
    private final int[] maxb;
    private final double[] minu;
    private final double[] maxu;
    private final double[] minv;
    private final double[] maxv;
    private boolean useColor = true;
    private boolean fillFaces = true;
    private boolean ambientOcclusion = false;
    private boolean faceOrderFlip = false;
    private final double rotAngle = 0;
    private final double scaleRate = 0.8;
    private boolean drawEdges = true;
    private WritableImage renderImage;
    double rotateAngle;
    double scale;
    double transX;
    double transY;
    private Point preMousePoint;
    private final Affine affineTransform;

    private OrigamiModel origamiModel = null;
    private OverlapRelationList overlapRelationList = null;
    private RectangleDomain domain;

    public FoldedModelScreen() {
        BUFFERW = 600;
        BUFFERH = 600;

        this.setWidth(BUFFERW);
        this.setHeight(BUFFERH);

        pbuf = new int[BUFFERW * BUFFERH];
        zbuf = new int[BUFFERW * BUFFERH];
        min = new int[BUFFERH];
        max = new int[BUFFERH];
        minr = new int[BUFFERH];
        maxr = new int[BUFFERH];
        ming = new int[BUFFERH];
        maxg = new int[BUFFERH];
        minb = new int[BUFFERH];
        maxb = new int[BUFFERH];
        maxu = new double[BUFFERH];
        maxv = new double[BUFFERH];
        minu = new double[BUFFERH];
        minv = new double[BUFFERH];

        clear();
        drawOrigami();
        rotateAngle = 0;
        scale = 1.0;
        affineTransform = new Affine();
        updateAffineTransform();

        this.setOnMousePressed(mouseEvent -> preMousePoint = new Point(mouseEvent.getX(), mouseEvent.getY()));
        this.setOnMouseMoved(mouseEvent -> {
            if (!mouseEvent.isSecondaryButtonDown() && !mouseEvent.isPrimaryButtonDown()) {
                setCursor(Cursor.DEFAULT);
            }
        });
        this.setOnMouseDragged(mouseEvent -> {
            if (mouseEvent.isSecondaryButtonDown()) {
                setCursor(Cursor.H_RESIZE);
                rotateAngle -= (mouseEvent.getX() - preMousePoint.getX()) / 5.0;
                preMousePoint = new Point(mouseEvent.getX(), mouseEvent.getY());
                updateAffineTransform();
                paintComponent();
            } else if (mouseEvent.isPrimaryButtonDown()) {
                setCursor(Cursor.CLOSED_HAND);
                transX += (mouseEvent.getX() - preMousePoint.getX()) / scale;
                transY += (mouseEvent.getY() - preMousePoint.getY()) / scale;
                preMousePoint = new Point(mouseEvent.getX(), mouseEvent.getY());
                updateAffineTransform();
                paintComponent();
            }
        });
        this.setOnScroll(scrollEvent -> {
            scale *= scrollEvent.getDeltaY() < 0 ? 0.9 : 1.1;
            updateAffineTransform();
            paintComponent();
        });

    }

    private void resetViewMatrix() {
        rotateAngle = 0;
        scale = 1;

        var folderTool = new FolderTool();
        domain = folderTool.createDomainOfFoldedModel(origamiModel.getFaces());

        updateAffineTransform();
        redrawOrigami();
    }

    public void redrawOrigami() {
        clear();
        drawOrigami();
        paintComponent();
        // repaint();
    }

    public void setUseColor(final boolean b) {
        useColor = b;
        redrawOrigami();
    }

    public void setFillFace(final boolean bFillFace) {
        fillFaces = bFillFace;
        redrawOrigami();
    }

    public void drawEdge(final boolean bEdge) {
        drawEdges = bEdge;
        redrawOrigami();
    }

    public void flipFaces(final boolean bFlip) {
        this.faceOrderFlip = bFlip;
        redrawOrigami();
    }

    public void shadeFaces(final boolean bShade) {
        ambientOcclusion = bShade;
        redrawOrigami();
    }

    private int getIndex(final int x, final int y) {
        return y * BUFFERW + x;
    }

    public void clear() {
        for (int i = 0; i < BUFFERW * BUFFERH; i++) {
            pbuf[i] = 0xffffffff;
            zbuf[i] = -1;
        }
        paintComponent();
    }

    private void updateAffineTransform() {
        bufferGraphicsContext.setTransform(new Affine());
        bufferGraphicsContext.translate(getWidth() * 0.5, getHeight() * 0.5);
        bufferGraphicsContext.scale(scale, -scale);
        bufferGraphicsContext.translate(transX, -transY);
        bufferGraphicsContext.rotate(rotateAngle);
        bufferGraphicsContext.translate(-getWidth() * 0.5, -getHeight() * 0.5);
    }

    public void setModel(final FoldedModel foldedModel) {
        this.origamiModel = foldedModel.getOrigamiModel();
        this.overlapRelationList = foldedModel.getOverlapRelationList();

        resetViewMatrix();
        redrawOrigami();
    }

    public void paintComponent() {
        // super.paintComponent(g);

        if (bufferGraphicsContext == null) {
            bufferGraphicsContext = this.getGraphicsContext2D();
            updateAffineTransform();
        }
        bufferGraphicsContext.setImageSmoothing(true);
        // bufferGraphicsContext.setTransform(new Affine());

        // Clear image
        // bufferGraphicsContext.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
        bufferGraphicsContext.setFill(Color.WHITE);
        bufferGraphicsContext.fillRect(0, 0, getWidth(), getHeight());

        // bufferGraphicsContext.setTransform(affineTransform);

        if (renderImage != null) {
            bufferGraphicsContext.drawImage(renderImage, 0, 0);
        } else {
            bufferGraphicsContext.setFill(Color.WHITE);
            bufferGraphicsContext.fillRect(0, 0, getWidth(), getHeight());
        }
    }

    public void drawOrigami() {
        if (origamiModel == null || overlapRelationList == null) {
            return;
        }

        if (!origamiModel.isFolded()) {
            return;
        }

        long time0 = System.currentTimeMillis();

        Vector2d center = new Vector2d(domain.getCenterX(), domain.getCenterY());
        final double localScale = scaleRate * Math.min(
                BUFFERW / (domain.getWidth()),
                BUFFERH / (domain.getHeight())) * 0.95;
        final double angle = rotAngle * Math.PI / 180;

        List<OriFace> faces = origamiModel.getFaces();
        for (OriFace face : faces) {

            face.trianglateAndSetColor(useColor, isFaceOrderFlipped(),
                    origamiModel.getPaperSize());

            face.triangleStream().forEach(tri -> {
                for (int i = 0; i < 3; i++) {

                    double x = (tri.v[i].p.x - center.x) * localScale;
                    double y = (tri.v[i].p.y - center.y) * localScale;

                    tri.v[i].p.x = x * Math.cos(angle) + y * Math.sin(angle) + BUFFERW * 0.5;
                    tri.v[i].p.y = x * Math.sin(angle) - y * Math.cos(angle) + BUFFERW * 0.5;

                }
                drawTriangle(tri, face.getFaceID());
            });
        }

        if (drawEdges) {
            // apply Sobel filter
            for (int y = 1; y < BUFFERH - 1; y++) {
                for (int x = 1; x < BUFFERW - 1; x++) {
                    int val_h = -1 * zbuf[getIndex(x - 1, y - 1)]
                            + zbuf[getIndex(x + 1, y - 1)]
                            + -2 * zbuf[getIndex(x - 1, y)]
                            + 2 * zbuf[getIndex(x + 1, y)]
                            + -1 * zbuf[getIndex(x - 1, y + 1)]
                            + zbuf[getIndex(x + 1, y + 1)];
                    int val_v = -1 * zbuf[getIndex(x - 1, y - 1)]
                            + zbuf[getIndex(x - 1, y + 1)]
                            + -2 * zbuf[getIndex(x, y - 1)]
                            + 2 * zbuf[getIndex(x, y + 1)]
                            + -1 * zbuf[getIndex(x + 1, y - 1)]
                            + zbuf[getIndex(x + 1, y + 1)];

                    if (val_h != 0 || val_v != 0) {
                        pbuf[getIndex(x, y)] = 0xff888888;
                    }
                }
            }
        }

        if (ambientOcclusion) {
            int renderFace = isFaceOrderFlipped() ? OverlapRelationValues.UPPER
                    : OverlapRelationValues.LOWER;
            int r = 10;
            int s = (int) (r * r * Math.PI);
            // For every pixel
            for (int y = 1; y < BUFFERH - 1; y++) {
                for (int x = 1; x < BUFFERW - 1; x++) {
                    int f_id = zbuf[getIndex(x, y)];

                    // Within a circle of radius r, Count the pixels of the
                    // surface
                    // that is above their own
                    int cnt = 0;
                    for (int dy = -r; dy <= r; dy++) {
                        for (int dx = -r; dx <= r; dx++) {
                            if (dx * dx + dy * dy > r * r) {
                                continue;
                            }
                            if (y + dy < 0 || y + dy > BUFFERH - 1) {
                                continue;
                            }
                            if (x + dx < 0 || x + dx > BUFFERW - 1) {
                                continue;
                            }
                            int f_id2 = zbuf[getIndex(x + dx, y + dy)];

                            if (f_id == -1 && f_id2 != -1) {
                                cnt++;
                            } else {
                                int[][] overlapRelation = overlapRelationList.getOverlapRelation();

                                if (f_id2 != -1 && overlapRelation[f_id][f_id2] == renderFace) {
                                    cnt++;
                                }
                            }
                        }
                    }

                    if (cnt > 0) {
                        int prev = pbuf[getIndex(x, y)];
                        double ratio = 1.0 - ((double) cnt) / s;
                        int p_r = (int) Math.max(0, ((prev & 0x00ff0000) >> 16) * ratio);
                        int p_g = (int) Math.max(0, ((prev & 0x0000ff00) >> 8) * ratio);
                        int p_b = (int) Math.max(0, (prev & 0x000000ff) * ratio);

                        pbuf[getIndex(x, y)] = (p_r << 16) | (p_g << 8) | p_b | 0xff000000;
                    }

                }
            }

        }
        long time1 = System.currentTimeMillis();
        System.out.println("render time = " + (time1 - time0) + "ms");

        renderImage = new WritableImage(BUFFERW, BUFFERH);
        renderImage.getPixelWriter().setPixels(0, 0, BUFFERW, BUFFERH, PixelFormat.getIntArgbInstance(), pbuf, 0, BUFFERW);
        paintComponent();
    }

    // --------------------------------------------------------------------
    // Polygon drawing
    //
    // --------------------------------------------------------------------
    private void drawTriangle(final TriangleFace tri, final int id) {

        // (For speed) set the range of use of the buffer
        int top = Integer.MAX_VALUE; // Integer.MAX_VALUE;
        int btm = Integer.MIN_VALUE;// Integer.MIN_VALUE;
        if (top > (int) tri.v[0].p.y) {
            top = (int) tri.v[0].p.y;
        }
        if (top > (int) tri.v[1].p.y) {
            top = (int) tri.v[1].p.y;
        }
        if (top > (int) tri.v[2].p.y) {
            top = (int) tri.v[2].p.y;
        }
        if (btm < (int) tri.v[0].p.y) {
            btm = (int) tri.v[0].p.y;
        }
        if (btm < (int) tri.v[1].p.y) {
            btm = (int) tri.v[1].p.y;
        }
        if (btm < (int) tri.v[2].p.y) {
            btm = (int) tri.v[2].p.y;
        }
        if (top < 0) {
            top = 0;
        }
        if (btm > BUFFERH) {
            btm = BUFFERH;
        }

        // Maximum and minimum buffer initialization
        for (int i = top; i < btm; i++) {
            min[i] = Integer.MAX_VALUE;
            max[i] = Integer.MIN_VALUE;
        }

        ScanEdge(tri.v[0], tri.v[1]);
        ScanEdge(tri.v[1], tri.v[2]);
        ScanEdge(tri.v[2], tri.v[0]);

        // To be drawn on the basis of the maximum and minimum buffer.
        for (int y = top; y < btm; y++) {

            // Skip if the buffer is not updated
            if (min[y] == Integer.MAX_VALUE) {
                continue;
            }

            int offset = y * BUFFERW;

            // Increment calculation
            int l = (max[y] - min[y]) + 1;
            int addr = (maxr[y] - minr[y]) / l;
            int addg = (maxg[y] - ming[y]) / l;
            int addb = (maxb[y] - minb[y]) / l;
            double addu = (maxu[y] - minu[y]) / l;
            double addv = (maxv[y] - minv[y]) / l;

            int r = minr[y];
            int g = ming[y];
            int b = minb[y];
            double u = minu[y];
            double v = minv[y];

            for (int x = min[y]; x <= max[y]; x++, r += addr, g += addg, b += addb, u += addu, v += addv) {

                if (x < 0 || x >= BUFFERW) {
                    continue;
                }

                // flattened pixel index
                int p = offset + x;

                int renderFace = isFaceOrderFlipped() ? OverlapRelationValues.UPPER
                        : OverlapRelationValues.LOWER;

                int[][] overlapRelation = overlapRelationList.getOverlapRelation();

                if (zbuf[p] == -1 || overlapRelation[zbuf[p]][id] == renderFace) {

                    int tr = r >> 16;
                    int tg = g >> 16;
                    int tb = b >> 16;

                    if (!fillFaces) {
                        pbuf[p] = 0xffffffff;
                    } else {
                        pbuf[p] = (tr << 16) | (tg << 8) | tb | 0xff000000;
                    }
                    zbuf[p] = id;
                }
            }
        }
    }

    // --------------------------------------------------------------------
    // ScanEdge
    //
    // Vector v1 ...Starting point
    // Vector v2 ...Starting point
    // --------------------------------------------------------------------
    private void ScanEdge(final TriangleVertex v1, final TriangleVertex v2) {

        int l = Math.abs((int) (v2.p.y - v1.p.y)) + 1;

        // Increment calculation
        int addx = (int) ((v2.p.x - v1.p.x) * 0xffff) / l;
        int addy = (int) ((v2.p.y - v1.p.y) * 0xffff) / l;

        int addr = (int) (255 * 0xffff * (v2.color.x - v1.color.x) / l);
        int addg = (int) (255 * 0xffff * (v2.color.y - v1.color.y) / l);
        int addb = (int) (255 * 0xffff * (v2.color.z - v1.color.z) / l);

        double addu = (v2.uv.x - v1.uv.x) / l;
        double addv = (v2.uv.y - v1.uv.y) / l;

        // Initial value setting
        int x = (int) (v1.p.x * 0xffff);
        int y = (int) (v1.p.y * 0xffff);
        int r = (int) (255 * 0xffff * v1.color.x);
        int g = (int) (255 * 0xffff * v1.color.y);
        int b = (int) (255 * 0xffff * v1.color.z);
        double u = v1.uv.x;
        double v = v1.uv.y;

        // Scan
        for (int i = 0; i < l; i++, x += addx, y += addy, r += addr, g += addg, b += addb, u += addu, v += addv) {
            int py = y >> 16;
            int px = x >> 16;

            if (py < 0 || py >= BUFFERH) {
                continue;
            }

            if (min[py] > px) {
                min[py] = px;
                minr[py] = r;
                ming[py] = g;
                minb[py] = b;
                minu[py] = u;
                minv[py] = v;
            }

            if (max[py] < px) {
                max[py] = px;
                maxr[py] = r;
                maxg[py] = g;
                maxb[py] = b;
                maxu[py] = u;
                maxv[py] = v;
            }
        }
    }

    public void setScale(final double newScale) {
        scale = newScale;
    }
    public boolean isFaceOrderFlipped() {
        return faceOrderFlip;
    }
}
