package net.omplanet.hexagonalgrids.ui;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import net.omplanet.hexagonalgrids.R;
import net.omplanet.hexagonalgrids.model.Cube;
import net.omplanet.hexagonalgrids.model.Grid;
import net.omplanet.hexagonalgrids.model.Hex;
import net.omplanet.hexagonalgrids.model.DemoObjects;
import net.omplanet.hexagonalgrids.model.StorageMap;

public class MainActivity extends ActionBarActivity {

    private RelativeLayout mRelativeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRelativeLayout = (RelativeLayout) findViewById(R.id.gridLayout);

        Grid.Shape shape = Grid.Shape.HEXAGON_POINTY_TOP;
        int radius = 3;

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            radius = extras.getInt("GRID_RADIUS", 3);
            shape = Grid.Shape.valueOf(extras.getString("GRID_SHAPE"));
            if (shape == null) {
                radius = 3;
                shape = Grid.Shape.HEXAGON_POINTY_TOP;
            }
        }

        initGridView(radius, shape);
    }

    private void initGridView(int radius, Grid.Shape shape) {
        int scale = setGridDimensions(radius, shape);

        //Init node elements
        Grid grid = setGridNodes(radius, scale, shape);

        //Init zoom buttons
        setGridButtons(grid);
    }

    private int setGridDimensions(int radius, Grid.Shape shape) {
        // Gets the layout params that will allow to resize the layout
        ViewGroup.LayoutParams params = mRelativeLayout.getLayoutParams();

        //Get display metrics
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int displayWidth = size.x;
        int displayHeight = size.y;

        //If in landscape mode, keep the width small as in portrait mode
        if(displayWidth > displayHeight) displayWidth = displayHeight;

        int horizontalPadding = (int) getResources().getDimension(R.dimen.activity_horizontal_margin);
        //int horizontalPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, horizontalPaddingInDp, getResources().getDisplayMetrics());
        displayWidth -= 2 * horizontalPadding;

        // Calculate the scale: the radius of single node.
        int scale = (int) (displayWidth / ((2*radius + 1) * (Math.sqrt(3))));

        // Changes the height and width of the grid to the specified *pixels*
        params.width = Grid.getGridWidth(radius, scale, shape);
        params.height = Grid.getGridHeight(radius, scale, shape);

        return scale;
    }

    private void setGridButtons(final Grid grid) {
        int scale = Grid.getGridWidth(grid.radius, grid.scale, grid.shape) / 16;

        View zoomOutButton = findViewById(R.id.zoomOutButton);
        ViewGroup.LayoutParams params = zoomOutButton.getLayoutParams();
        params.width = scale;
        params.height = scale;

        View zoomInButton = findViewById(R.id.zoomInButton);
        params = zoomInButton.getLayoutParams();
        params.width = scale;
        params.height = scale;

        zoomOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int newRadius = grid.radius+1;
                if(newRadius > 12) return;

                //Restart the activity with the new parameters
                Intent intent = new Intent(MainActivity.this, MainActivity.class);
                intent.putExtra("GRID_RADIUS", newRadius);
                intent.putExtra("GRID_SHAPE", grid.shape.name());
                startActivity(intent);
                finish();

                //Remove all the elements from the view except the side buttons
//                final ViewGroup viewGroup = (ViewGroup) findViewById(R.id.container_layout);
//                viewGroup.removeAllViews();
//                mRelativeLayout = (RelativeLayout) View.inflate(MainActivity.this, R.layout.hex_grid_layout, null);
//                viewGroup.addView(mRelativeLayout);
//                viewGroup.invalidate();
            }
        });

        zoomInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int newRadius = grid.radius-1;
                if(newRadius < 0) return;

                //Restart the activity with the new parameters
                Intent intent = new Intent(MainActivity.this, MainActivity.class);
                intent.putExtra("GRID_RADIUS", newRadius);
                intent.putExtra("GRID_SHAPE", grid.shape.name());
                startActivity(intent);
                finish();

                //Remove all the elements from the view except the side buttons
//                mRelativeLayout.removeAllViews();
//                initGridView(newRadius, grid.shape);
//                mRelativeLayout.invalidate();
            }
        });
    }

    private Grid setGridNodes(int radius, int scale, Grid.Shape shape) {
        try {
            StorageMap storageMap = new StorageMap(radius, shape, DemoObjects.squareMap);
            final Grid grid = new Grid(radius, scale, shape);

            //Gird node listener restricted to the node's circular area.
            View.OnTouchListener gridNodeTouchListener = new View.OnTouchListener() {

                @Override
                public boolean onTouch(final View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            float xPoint = event.getX();
                            float yPoint = event.getY();
                            //Hex hex = grid.pixelToHex(event.getX(), event.getY()); //This can work on the RelativeLayout grid area
                            boolean isPointOutOfCircle = (grid.centerOffsetX -xPoint)*(grid.centerOffsetX -xPoint) + (grid.centerOffsetY -yPoint)*(grid.centerOffsetY -yPoint) > grid.width * grid.width / 4;

                            if (isPointOutOfCircle) return false;
                            else v.setSelected(true);
                            break;
                        case MotionEvent.ACTION_OUTSIDE:
                            break;
                        case MotionEvent.ACTION_CANCEL:
                            break;
                        case MotionEvent.ACTION_MOVE:
                            break;
                        case MotionEvent.ACTION_SCROLL:
                            break;
                        case MotionEvent.ACTION_UP:
                            v.setSelected(false);
                            CircleImageView view = (CircleImageView) v;
                            OnGridHexClick(view.getHex());
                            break;
                    }
                    return true;
                }
            };

            for(Cube cube : grid.nodes) {
                Hex hex = null;
                switch (shape) {
                    case HEXAGON_POINTY_TOP:
                        hex = cube.toHex();
                        break;
                    case RECTANGLE:
                        hex = cube.cubeToOddRHex();
                        break;
                }

                CircleImageView view = new CircleImageView(this);
                Integer pic = (Integer) storageMap.getObjectByCoordinate(hex.getQ(), hex.getR());
                if(pic == null) {
                    view.setHex(hex);
                    view.setOnTouchListener(gridNodeTouchListener);
//                    view.setBackgroundResource(R.drawable.ring);
                    view.setBackgroundResource(R.drawable.empty_image);
                } else {
                    view = new CircleImageView(this);
                    //view.setBackgroundResource(R.drawable.hexagon);
                    view.setOnTouchListener(gridNodeTouchListener);
                    view.setHex(hex);
                    if(pic != 0) {
                        view.setImageResource(pic);
                    } else {
                        view.setImageResource(R.drawable.no_profile_image);
                    }
                }
                addViewToLayout(view, hex, grid);
            }

            return grid;
        } catch (Exception e) {
            Toast.makeText(MainActivity.this, "Sorry, there was a problem initializing the application.", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

        return null;
    }

    private void addViewToLayout(View view, Hex hex, Grid grid) {
        //Add to view
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(grid.width, grid.height);
        params.addRule(RelativeLayout.RIGHT_OF, R.id.centerLayout);
        params.addRule(RelativeLayout.BELOW, R.id.centerLayout);
        mRelativeLayout.addView(view, params);

        //Set coordinates
        Point p = grid.hexToPixel(hex);
        switch (grid.shape) {
            case HEXAGON_POINTY_TOP:
                params.leftMargin = -grid.centerOffsetX + p.x;
                params.topMargin = -grid.centerOffsetY + p.y;
                break;
            case RECTANGLE:
                params.leftMargin = -grid.width * grid.radius -grid.centerOffsetX + p.x;
                params.topMargin = (int) (-1.5 * grid.scale * grid.radius -grid.centerOffsetY + p.y);
                break;
        }
    }

    private void OnGridHexClick(Hex hex) {
        Toast.makeText(MainActivity.this, "OnGridHexClick: " + hex, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
