package bsplib.struct;

import bsplib.entity.*;
import java.util.*;

/**
 * Data structure of the BSP file and their lumps.
 * It doesn't cover all known lumps yet. Only those that are used by
 * BSPSource are currently implemented.
 *
 * Yes, there are NO getters and setters and NO final fields!
 * Encapsulation just doesn't make much sense in this case...
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class BspData {

    public List<DAreaportal> areaportals;
    public List<DBrush> brushes;
    public List<DBrushSide> brushSides;
    public List<DCubemapSample> cubemaps;
    public List<DDispInfo> dispinfos;
    public List<DDispTri> disptris;
    public List<DDispVert> dispverts;
    public List<DDispMultiBlend> dispmultiblend;
    public List<DEdge> edges;
    public List<DFace> faces;
    public List<DFace> origFaces;
    public List<DLeaf> leaves;
    public List<DModel> models;
    public List<DNode> nodes;
    public List<DOccluderData> occluderDatas;
    public List<DOccluderPolyData> occluderPolyDatas;
    public List<DOverlay> overlays;
    public List<DOverlayFade> overlayFades;
    public List<DOverlaySystemLevel> overlaySysLevels;
    public List<DPlane> planes;
    public List<DPrimitive> prims;
    public List<Integer> primIndices;
    public List<DVertex> primVerts;
    public List<DStaticProp> staticProps;
    public List<DTexData> texdatas;
    public List<DTexInfo> texinfos;
    public List<DVertex> clipPortalVerts;
    public List<DVertex> verts;
    public List<Entity> entities;
    public List<Integer> leafBrushes;
    public List<Integer> leafFaces;
    public List<Integer> occluderVerts;
    public List<Integer> surfEdges;
    public List<String> staticPropName;
    public List<Integer> staticPropLeaf;
    public List<String> texnames;
    public Set<LevelFlag> mapFlags;

}
