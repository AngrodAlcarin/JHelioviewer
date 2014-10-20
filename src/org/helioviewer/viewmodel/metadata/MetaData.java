package org.helioviewer.viewmodel.metadata;

import org.helioviewer.base.math.RectangleDouble;
import org.helioviewer.base.math.Vector2dDouble;
import org.helioviewer.base.math.Vector2dInt;
import org.helioviewer.viewmodel.region.Region;
import org.helioviewer.viewmodel.region.StaticRegion;
import org.helioviewer.viewmodel.view.jp2view.datetime.ImmutableDateTime;

public abstract class MetaData extends Exception{

    private Vector2dDouble lowerLeftCorner;
    private Vector2dDouble sizeVector;

    protected MetaDataContainer metaDataContainer = null;
    protected String instrument = "";
    protected String detector = "";
    protected String measurement = " ";
    protected String observatory = " ";
    protected String fullName = "";
    protected Vector2dInt pixelImageSize = new Vector2dInt();
    protected double solarPixelRadius = -1;
    protected Vector2dDouble sunPixelPosition = new Vector2dDouble();

    protected double meterPerPixel;
    protected ImmutableDateTime time;

    protected double innerRadius;
    protected double outerRadius;
    protected double flatDistance;
    protected double maskRotation;
    protected Vector2dDouble occulterCenter;
    
    
    protected double heeqX;
    protected double heeqY;
    protected double heeqZ;
    protected boolean heeqAvailable = false;

    protected double heeX;
    protected double heeY;
    protected double heeZ;
    protected boolean heeAvailable = false;

    protected double crlt;
    protected double crln;
    protected double dobs;
    protected boolean carringtonAvailable = false;

    protected double stonyhurstLongitude;
    protected double stonyhurstLatitude;
    protected boolean stonyhurstAvailable = false;

    
    protected boolean hasCorona;
    protected boolean hasSphere;
    
    
    /**
     * Default constructor, does not set size or position.
     */
    public MetaData(MetaDataContainer metaDataContainer) {
        lowerLeftCorner = null;
        sizeVector = null;
        
        if (metaDataContainer.get("INSTRUME") == null)
            return;

        detector = metaDataContainer.get("DETECTOR");
        instrument = metaDataContainer.get("INSTRUME");

        if (detector == null) {
            detector = " ";
        }
        if (instrument == null) {
            instrument = " ";
        }

    }

    /**
     * Constructor, setting size and position.
     * 
     * @param newLowerLeftCorner
     *            Physical lower left corner of the corresponding image
     * @param newSizeVector
     *            Physical size of the corresponding image
     */
    public MetaData(Vector2dDouble newLowerLeftCorner, Vector2dDouble newSizeVector) {
        lowerLeftCorner = newLowerLeftCorner;
        sizeVector = newSizeVector;
    }

    /**
     * Constructor, setting size and position.
     * 
     * @param newLowerLeftCornerX
     *            Physical lower left x-coordinate of the corresponding image
     * @param newLowerLeftCornerY
     *            Physical lower left y-coordinate of the corresponding image
     * @param newWidth
     *            Physical width of the corresponding image
     * @param newHeight
     *            Physical height of the corresponding image
     */
    public MetaData(double newLowerLeftCornerX, double newLowerLeftCornerY, double newWidth, double newHeight) {
        lowerLeftCorner = new Vector2dDouble(newLowerLeftCornerX, newLowerLeftCornerY);
        sizeVector = new Vector2dDouble(newWidth, newHeight);
    }

    /**
     * Constructor, setting size and position.
     * 
     * @param newLowerLeftCorner
     *            Physical lower left corner of the corresponding image
     * @param newWidth
     *            Physical width of the corresponding image
     * @param newHeight
     *            Physical height of the corresponding image
     */
    public MetaData(Vector2dDouble newLowerLeftCorner, double newWidth, double newHeight) {
        lowerLeftCorner = newLowerLeftCorner;
        sizeVector = new Vector2dDouble(newWidth, newHeight);
    }

    /**
     * Constructor, setting size and position.
     * 
     * @param newLowerLeftCornerX
     *            Physical lower left x-coordinate of the corresponding image
     * @param newLowerLeftCornerY
     *            Physical lower left y-coordinate of the corresponding image
     * @param newSizeVector
     *            Physical size of the corresponding image
     */
    public MetaData(double newLowerLeftCornerX, double newLowerLeftCornerY, Vector2dDouble newSizeVector) {
        lowerLeftCorner = new Vector2dDouble(newLowerLeftCornerX, newLowerLeftCornerY);
        sizeVector = newSizeVector;
    }

    /**
     * Constructor, setting size and position.
     * 
     * @param newRectangle
     *            Full physical rectangle of the corresponding image
     */
    public MetaData(RectangleDouble newRectangle) {
        lowerLeftCorner = newRectangle.getLowerLeftCorner();
        sizeVector = newRectangle.getSize();
    }

    /**
     * Copy constructor
     * 
     * @param original
     *            Object to copy
     */
    public MetaData(MetaData original) {
        lowerLeftCorner = new Vector2dDouble(original.lowerLeftCorner);
        sizeVector = new Vector2dDouble(original.sizeVector);
    }

    /**
     * {@inheritDoc}
     */
    public synchronized Vector2dDouble getPhysicalImageSize() {
        return sizeVector;
    }

    /**
     * {@inheritDoc}
     */
    public synchronized Vector2dDouble getPhysicalLowerLeft() {
        return lowerLeftCorner;
    }

    /**
     * {@inheritDoc}
     */
    public synchronized double getPhysicalImageHeight() {
        return sizeVector.getY();
    }

    /**
     * {@inheritDoc}
     */
    public synchronized double getPhysicalImageWidth() {
        return sizeVector.getX();
    }

    /**
     * {@inheritDoc}
     */
    public synchronized Vector2dDouble getPhysicalLowerRight() {
        return lowerLeftCorner.add(sizeVector.getXVector());
    }

    /**
     * {@inheritDoc}
     */
    public synchronized Vector2dDouble getPhysicalUpperLeft() {
        return lowerLeftCorner.add(sizeVector.getYVector());
    }

    /**
     * {@inheritDoc}
     */
    public synchronized Vector2dDouble getPhysicalUpperRight() {
        return lowerLeftCorner.add(sizeVector);
    }

    /**
     * {@inheritDoc}
     */
    public synchronized RectangleDouble getPhysicalRectangle() {
        return new RectangleDouble(lowerLeftCorner, sizeVector);
    }

    /**
     * {@inheritDoc}
     */
    public synchronized Region getPhysicalRegion() {
        return StaticRegion.createAdaptedRegion(lowerLeftCorner, sizeVector);
    }

    /**
     * Sets the physical size of the corresponding image.
     * 
     * @param newImageSize
     *            Physical size of the corresponding image
     */
    protected synchronized void setPhysicalImageSize(Vector2dDouble newImageSize) {
        sizeVector = newImageSize;
    }

    /**
     * Sets the physical lower left corner the corresponding image.
     * 
     * @param newlLowerLeftCorner
     *            Physical lower left corner the corresponding image
     */
    protected synchronized void setPhysicalLowerLeftCorner(Vector2dDouble newlLowerLeftCorner) {
        lowerLeftCorner = newlLowerLeftCorner;
    }
    
    /**
     * {@inheritDoc}
     */
    public String getDetector() {
        return detector;
    }

    /**
     * {@inheritDoc}
     */
    public String getInstrument() {
        return instrument;
    }

    /**
     * {@inheritDoc}
     */
    public String getMeasurement() {
        return measurement;
    }

    /**
     * {@inheritDoc}
     */
    public String getObservatory() {
        return observatory;
    }

    public String getFullName() {
        return fullName;
    }

    /**
     * {@inheritDoc}
     */
    public double getSunPixelRadius() {
        return solarPixelRadius;
    }

    /**
     * {@inheritDoc}
     */
    public Vector2dDouble getSunPixelPosition() {
        return sunPixelPosition;
    }

    /**
     * {@inheritDoc}
     */
    public Vector2dInt getResolution() {
        return pixelImageSize;
    }

    /**
     * {@inheritDoc}
     */
    public double getUnitsPerPixel() {
        return meterPerPixel;
    }

    /**
     * {@inheritDoc}
     */
    public ImmutableDateTime getDateTime() {
        return time;
    }
    
    abstract boolean updatePixelParameters();

	public boolean hasSphere() {
		// TODO Auto-generated method stub
		return hasSphere;
	}

	public boolean hasCorona() {
		// TODO Auto-generated method stub
		return hasCorona;
	}

	public double getHEEX() {
        return heeX;
    }

    public double getHEEY() {
        return heeqY;
    }

    public double getHEEZ() {
        return heeZ;
    }

    public boolean isHEEProvided() {
        return heeAvailable;
    }

    public double getHEEQX() {
        return this.heeqX;
    }

    public double getHEEQY() {
        return this.heeqY;
    }

    public double getHEEQZ() {
        return this.heeqZ;
    }

    public boolean isHEEQProvided() {
        return this.heeqAvailable;
    }

    public double getCrln() {
        return crln;
    }

    public double getCrlt() {
        return crlt;
    }

    public double getDobs() {
        return dobs;
    }

    public boolean isCarringtonProvided() {
        return carringtonAvailable;
    }

    public boolean isStonyhurstProvided() {
        return stonyhurstAvailable;
    }

    public double getStonyhurstLatitude() {
        return stonyhurstLatitude;
    }

    public double getStonyhurstLongitude() {
        return stonyhurstLongitude;
    }
    
    /**
     * {@inheritDoc}
     */
    public double getInnerPhysicalOcculterRadius() {
        return innerRadius;
    }

    /**
     * {@inheritDoc}
     */
    public double getOuterPhysicalOcculterRadius() {
        return outerRadius;
    }

    /**
     * {@inheritDoc}
     */
    public double getPhysicalFlatOcculterSize() {
        return flatDistance;
    }

    /**
     * {@inheritDoc}
     */
    public double getMaskRotaton() {
        return maskRotation;
    }

    public Vector2dDouble getOcculterCenter() {
        return occulterCenter;
    }

	public double getMaskRotation() {
		// TODO Auto-generated method stub
		return 0;
	}

	public boolean checkForModifications() {
        boolean changed = updatePixelParameters();

        double currentMaskRotation = Math.toRadians(metaDataContainer.tryGetDouble("CROTA"));
        System.out.println("currentMaskRotation : " + currentMaskRotation);
        if (changed || Math.abs(maskRotation - currentMaskRotation) > Math.toRadians(1)) {
            maskRotation = currentMaskRotation;
            changed = true;
        }

        return changed;
	}

	public void updateDateTime(ImmutableDateTime newDateTime) {
        time = newDateTime;		
	}

}
