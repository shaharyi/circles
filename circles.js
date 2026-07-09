var MaxY, MaxX, pstep, DELAY_MS;
const PI = Math.PI;
var c = document.getElementById("myCanvas");
var ctx = c.getContext("2d");   
var rstart;
var buffImage;    
var colors = [];

function init(radius, step, delay)
{
    MaxY = radius * 5;
    MaxX = MaxY * 3 / 2;
    pstep = step;
    DELAY_MS = delay;
    rstart = radius - radius % pstep + 1; //so smallest will have radius 1
    drawCircles(rstart);
}

function start()
{
    animate(rstart);
}

function animate(r)
{
    if (r > 0)
    {
    	ctx.clearRect(0,0,MaxX,MaxY/2+r);
    	drawCircles(r - pstep);
    	bgImage = ctx.getImageData(0, 0, MaxX, MaxY);
        flatten(0, MaxX/2, MaxY/2, r, colors[r], bgImage);
	}
}

function flatten(dist, cx, cy, r, color, bgImage)
{
    var step = r / 10;                 
    var done = false;        
    var angle = PI/2 - dist/(r*PI)*PI;
    if (angle <= -PI/2) 
    {
    	done = true;
        rx = cx + PI*r;
        lx = cx - PI*r;
        angle = -PI/2;
    }
    ctx.putImageData(bgImage, 0, 0);
    ctx.strokeStyle = color;
    ctx.beginPath();
    ctx.arc(cx + dist, cy, r, -angle, PI/2);
    ctx.lineTo(cx - dist, cy + r);
    ctx.arc(cx - dist, cy, r, PI/2, PI + angle);
    ctx.stroke();    	    
    dist += step;
    if (!done)
    	setTimeout(flatten, DELAY_MS, dist, cx, cy, r, color, bgImage);
    else
    	animate(r - pstep);
}


function getRandColor(b){
	// Six levels of brightness from 0 to 5, 0 being the darkest
	var rgb = [1, 1, 1].map(function(x){
    	return ((x*256*Math.random()+b*51)/2) | 0;
    });
	return "rgb(" + rgb.join(",") + ")";
}

function drawCircles(r1)
{
	for (var r = r1; r > 0; r -= pstep)
    {
    	ctx.beginPath();
        if (!colors[r])
        	colors[r] = getRandColor(2);
		ctx.strokeStyle = colors[r];
		ctx.arc(MaxX/2, MaxY/2, r, 0, 2*PI);
        ctx.stroke();
	}
	image = ctx.getImageData(0, 0, MaxX, MaxY);        
    return image;
}

