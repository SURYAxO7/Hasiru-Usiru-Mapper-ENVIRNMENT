// MapVisualization Component - Interactive Geospatial Mapping
import React, { useEffect, useRef, useState } from 'react';
import './MapVisualization.css';

const MapVisualization = ({ zones = [] }) => {
  const canvasRef = useRef(null);
  const [zoom, setZoom] = useState(1);
  const [pan, setPan] = useState({ x: 0, y: 0 });

  useEffect(() => {
    drawMap();
  }, [zones, zoom, pan]);

  const drawMap = () => {
    const canvas = canvasRef.current;
    if (!canvas) return;

    const ctx = canvas.getContext('2d');
    const width = canvas.width;
    const height = canvas.height;

    // Clear canvas
    ctx.fillStyle = '#e8f5e9';
    ctx.fillRect(0, 0, width, height);

    // Draw grid
    drawGrid(ctx, width, height);

    // Draw zones
    zones.forEach(zone => {
      drawZone(ctx, zone, width, height);
    });

    // Draw legend
    drawLegend(ctx, width, height);
  };

  const drawGrid = (ctx, width, height) => {
    ctx.strokeStyle = '#d0d0d0';
    ctx.lineWidth = 0.5;

    for (let i = 0; i <= 10; i++) {
      const x = (i / 10) * width;
      const y = (i / 10) * height;

      ctx.beginPath();
      ctx.moveTo(x, 0);
      ctx.lineTo(x, height);
      ctx.stroke();

      ctx.beginPath();
      ctx.moveTo(0, y);
      ctx.lineTo(width, y);
      ctx.stroke();
    }
  };

  const drawZone = (ctx, zone, width, height) => {
    const x = ((zone.longitude + 180) / 360) * width + pan.x;
    const y = ((90 - zone.latitude) / 180) * height + pan.y;
    const radius = Math.sqrt(zone.area) * zoom;

    // Draw zone circle
    const color = getHealthColor(zone.healthScore);
    ctx.fillStyle = color;
    ctx.globalAlpha = 0.6;
    ctx.beginPath();
    ctx.arc(x, y, radius, 0, Math.PI * 2);
    ctx.fill();

    // Draw border
    ctx.globalAlpha = 1;
    ctx.strokeStyle = color;
    ctx.lineWidth = 2;
    ctx.stroke();

    // Draw label
    ctx.fillStyle = '#000';
    ctx.font = '12px Arial';
    ctx.textAlign = 'center';
    ctx.fillText(zone.name.substring(0, 10), x, y);
  };

  const drawLegend = (ctx, width, height) => {
    const legendX = width - 180;
    const legendY = 20;

    ctx.fillStyle = 'rgba(255, 255, 255, 0.9)';
    ctx.fillRect(legendX, legendY, 160, 120);
    ctx.strokeStyle = '#999';
    ctx.strokeRect(legendX, legendY, 160, 120);

    ctx.fillStyle = '#000';
    ctx.font = 'bold 12px Arial';
    ctx.fillText('Health Status', legendX + 80, legendY + 15);

    const statuses = [
      { color: '#4CAF50', label: 'Excellent (>75%)' },
      { color: '#FFC107', label: 'Good (50-75%)' },
      { color: '#F44336', label: 'Poor (<50%)' }
    ];

    statuses.forEach((status, index) => {
      const y = legendY + 35 + (index * 25);
      ctx.fillStyle = status.color;
      ctx.fillRect(legendX + 10, y, 15, 15);
      ctx.fillStyle = '#000';
      ctx.font = '11px Arial';
      ctx.textAlign = 'left';
      ctx.fillText(status.label, legendX + 30, y + 12);
    });
  };

  const getHealthColor = (score) => {
    if (score > 75) return '#4CAF50';
    if (score > 50) return '#FFC107';
    return '#F44336';
  };

  const handleZoom = (direction) => {
    setZoom(prev => direction === 'in' ? prev * 1.2 : prev / 1.2);
  };

  const handlePan = (direction) => {
    const step = 20;
    const newPan = { ...pan };
    switch(direction) {
      case 'up': newPan.y += step; break;
      case 'down': newPan.y -= step; break;
      case 'left': newPan.x += step; break;
      case 'right': newPan.x -= step; break;
    }
    setPan(newPan);
  };

  const handleReset = () => {
    setZoom(1);
    setPan({ x: 0, y: 0 });
  };

  return (
    <div className="map-visualization">
      <div className="map-controls">
        <button onClick={() => handleZoom('in')}>🔍+ Zoom In</button>
        <button onClick={() => handleZoom('out')}>🔍- Zoom Out</button>
        <button onClick={() => handlePan('up')}>⬆ Up</button>
        <button onClick={() => handlePan('down')}>⬇ Down</button>
        <button onClick={() => handlePan('left')}>⬅ Left</button>
        <button onClick={() => handlePan('right')}>➡ Right</button>
        <button onClick={handleReset}>↺ Reset</button>
      </div>
      <canvas 
        ref={canvasRef} 
        width={1000} 
        height={600}
        className="map-canvas"
      />
      <div className="map-info">
        <p>Zoom: {(zoom * 100).toFixed(0)}% | Pan: ({pan.x}, {pan.y})</p>
        <p>Total Zones: {zones.length}</p>
      </div>
    </div>
  );
};

export default MapVisualization;
