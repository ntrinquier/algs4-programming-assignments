import java.awt.Color;

public class SeamCarver {
    private Picture pic;
    private int w;
    private int h;
    private double[] distTo;
    private int[] edgeTo;
    private double[] weights;

    // API
    
    // create a seam carver object based on the given picture
    public SeamCarver(Picture picture) {
        this.pic = new Picture(picture);
        this.w = picture.width();
        this.h = picture.height();
    }
    
    // current picture
    public Picture picture() {
        Picture ret = new Picture(this.width(), this.height());
        
        for (int x = 0; x < ret.width(); x++) {
            for (int y = 0; y < ret.height(); y++) {
                ret.set(x, y, this.pic.get(x, y)); // copy of image with correct dimensions
            }
        }
        
        this.pic = ret;
        return this.pic;
    }
    
    // width of current picture
    public int width() {
        return this.w;
    }
    
    // height of current picture
    public int height() {
        return this.h;
    }
    
    // energy of pixel at column x and row y
    public double energy(int x, int y) {
        if (x < 0 || x >= this.width()) {
            throw new IndexOutOfBoundsException("x out of range");
        } else if (y < 0 || y >= this.height()) {
            throw new IndexOutOfBoundsException("y out of range");
        }
        
        // border pixels
        if (x == 0  || x == this.width()-1 || y == 0 || y == this.height()-1) {
            return 3*255*255;
        }
        
        // other pixels
        return squareGradient(this.pic.get(x-1, y), this.pic.get(x+1, y))+squareGradient(this.pic.get(x, y-1), this.pic.get(x, y+1));
    }
    
    // sequence of indices for vertical seam
    public int[] findVerticalSeam() {
        int numberOfPixels = this.width()*this.height();
        this.distTo = new double[numberOfPixels];
        this.edgeTo = new int[numberOfPixels];
        this.weights = new double[numberOfPixels];
        int[] ret = new int[this.height()];
        
        // init arrays: Very inefficient        
        for (int y = 0; y < this.height(); y++) {
            for (int x = 0; x < this.width(); x++) {
                int id = this.fromCoordinatesToId(x, y);
                
                if (y == 0) {
                    this.distTo[id] = 0;
                } else {
                    this.distTo[id] = Double.POSITIVE_INFINITY;
                }
                
                this.edgeTo[id] = -1;
                this.weights[id] = this.energy(x, y);
            }
        }
        
        // computation
        for (int y = 0; y < this.height()-1; y++) {
            for (int x = 0; x < this.width(); x++) {
                int id = this.fromCoordinatesToId(x, y);
                
                if (x > 0) {
                    this.relax(id, this.fromCoordinatesToId(x-1, y+1));
                }
                
                this.relax(id, this.fromCoordinatesToId(x, y+1));
                
                if (x < this.width()-1) {
                    this.relax(id, this.fromCoordinatesToId(x+1, y+1));
                }
            }
        }
        
        // find last pixel of the seam
        int idMin = this.fromCoordinatesToId(0, this.height()-1);
        
        for (int x = 1; x < this.width(); x++) {
            int id = this.fromCoordinatesToId(x, this.height()-1);
            if (distTo[id] < distTo[idMin]) {
                idMin = id;
            }
        }
        
        // build seam
        int id = idMin;
        while (id != -1) {
            int[] coordinates = this.fromIdToCoordinates(id);
            ret[coordinates[1]] = coordinates[0];
            id = this.edgeTo[id];
        }
        
        this.distTo = null;
        this.edgeTo = null;
        this.weights = null;
        
        return ret;
    }
    
    // sequence of indices for horizontal seam
    public int[] findHorizontalSeam() {
        int numberOfPixels = this.width()*this.height();
        this.distTo = new double[numberOfPixels];
        this.edgeTo = new int[numberOfPixels];
        this.weights = new double[numberOfPixels];
        int[] ret = new int[this.width()];
        
        // init arrays: Very inefficient        
        for (int y = 0; y < this.height(); y++) {
            for (int x = 0; x < this.width(); x++) {
                int id = this.fromCoordinatesToId(x, y);
                
                if (x == 0) {
                    this.distTo[id] = 0;
                } else {
                    this.distTo[id] = Double.POSITIVE_INFINITY;
                }
                
                this.edgeTo[id] = -1;
                this.weights[id] = this.energy(x, y);
            }
        }
        
        // computation
        for (int x = 0; x < this.width()-1; x++) {
            for (int y = 0; y < this.height(); y++) {
                int id = this.fromCoordinatesToId(x, y);
                
                if (y > 0) {
                    this.relax(id, this.fromCoordinatesToId(x+1, y-1));
                }
                
                this.relax(id, this.fromCoordinatesToId(x+1, y));
                
                if (y < this.height()-1) {
                    this.relax(id, this.fromCoordinatesToId(x+1, y+1));
                }
            }
        }
        
        // find last pixel of the seam
        int idMin = this.fromCoordinatesToId(this.width()-1, 0);
        
        for (int y = 1; y < this.height(); y++) {
            int id = this.fromCoordinatesToId(this.width()-1, y);
            if (distTo[id] < distTo[idMin]) {
                idMin = id;
            }
        }
        
        // build seam
        int id = idMin;
        while (id != -1) {
            int[] coordinates = this.fromIdToCoordinates(id);
            ret[coordinates[0]] = coordinates[1];
            id = this.edgeTo[id];
        }
        
        this.distTo = null;
        this.edgeTo = null;
        this.weights = null;
        
        return ret;
    }
    
    // remove vertical seam from current picture
    public void removeVerticalSeam(int[] seam) {
        if (seam == null) {
            throw new NullPointerException("seam is null");
        } else if (this.width() <= 1) {
            throw new IllegalArgumentException("width is inferior or equal to 1");
        } else if (seam.length != this.height()) {
            throw new IllegalArgumentException("wrong length for seam");
        }

        int prev = seam[0];
        
        for (int y = 0; y < this.height(); y++) {
            if (absDiff(prev, seam[y]) > 1) {
                throw new IllegalArgumentException("invalid seam");
            }
            
            prev = seam[y];
            for (int x = seam[y]; x < this.width()-1; x++) {
                this.pic.set(x, y, this.pic.get(x+1, y)); // update image
            }
        }
        
        w--;
    }
    
    // remove horizontal seam from current picture
    public void removeHorizontalSeam(int[] seam) {
        if (seam == null) {
            throw new NullPointerException("seam is null");
        } else if (this.height() <= 1) {
            throw new IllegalArgumentException("height is inferior or equal to 1");
        } else if (seam.length != this.width()) {
            throw new IllegalArgumentException("wrong length for seam");
        }

        int prev = seam[0];
        
        for (int x = 0; x < this.width(); x++) {
            if (absDiff(prev, seam[x]) > 1) {
                throw new IllegalArgumentException("invalid seam");
            }
            
            prev = seam[x];
            for (int y = seam[x]; y < this.height()-1; y++) {
                this.pic.set(x, y, this.pic.get(x, y+1)); // update image
            }
        }
        
        h--;
    }
    
    // Private methods
    
    private int absDiff(int x, int y) {
        if (x < y) {
            return y-x;
        } else {
            return x-y;
        }
    }
    
    private int squareGradient(Color a, Color b) {
        int redDelta = a.getRed()-b.getRed();
        int greenDelta = a.getGreen()-b.getGreen();
        int blueDelta = a.getBlue()-b.getBlue();
        
        return redDelta*redDelta+greenDelta*greenDelta+blueDelta*blueDelta;
    }
    
    private int fromCoordinatesToId(int x, int y) {
        return x+y*this.width();
    }
    
    private int[] fromIdToCoordinates(int id) {
        int[] ret = {id % this.width(), id/this.width()};
        return ret;
    }
    
    private void relax(int from, int to) {
        if (this.distTo[from]+this.weights[to] < this.distTo[to]) {
            this.distTo[to] = this.distTo[from]+this.weights[to];
            this.edgeTo[to] = from;
        }
    }
}