(() => {
  "use strict";

  /** Canvas を使う軽量チャート。Node 前端ビルドを導入せず、SSR HTML の値だけを描画する。 */
  function drawChart(canvas) {
    if (!canvas) return;
    const values = (canvas.dataset.values || "").replace(/[\[\]\s]/g, "").split(",")
      .filter(Boolean).map(Number);
    if (!values.length) return;
    const rect = canvas.getBoundingClientRect();
    const ratio = window.devicePixelRatio || 1;
    canvas.width = Math.max(600, rect.width * ratio);
    canvas.height = Math.max(220, rect.height * ratio);
    const ctx = canvas.getContext("2d");
    ctx.scale(ratio, ratio);
    const width = canvas.width / ratio;
    const height = canvas.height / ratio;
    const pad = { left: 36, right: 12, top: 14, bottom: 27 };
    const max = Math.max(...values, 1);

    ctx.strokeStyle = "#e8edf4";
    ctx.lineWidth = 1;
    for (let i = 0; i <= 4; i++) {
      const y = pad.top + (height - pad.top - pad.bottom) * i / 4;
      ctx.beginPath(); ctx.moveTo(pad.left, y); ctx.lineTo(width - pad.right, y); ctx.stroke();
    }
    const step = (width - pad.left - pad.right) / Math.max(values.length - 1, 1);
    const points = values.map((value, index) => ({
      x: pad.left + index * step,
      y: pad.top + (height - pad.top - pad.bottom) * (1 - value / max)
    }));
    const gradient = ctx.createLinearGradient(0, pad.top, 0, height - pad.bottom);
    gradient.addColorStop(0, "rgba(29,99,237,.28)"); gradient.addColorStop(1, "rgba(29,99,237,0)");
    ctx.beginPath(); ctx.moveTo(points[0].x, height - pad.bottom);
    points.forEach(point => ctx.lineTo(point.x, point.y));
    ctx.lineTo(points.at(-1).x, height - pad.bottom); ctx.closePath(); ctx.fillStyle = gradient; ctx.fill();
    ctx.beginPath(); points.forEach((point, index) => index ? ctx.lineTo(point.x, point.y) : ctx.moveTo(point.x, point.y));
    ctx.strokeStyle = "#1d63ed"; ctx.lineWidth = 2.5; ctx.stroke();
    points.forEach((point, index) => {
      if (index % 3 === 0 || index === points.length - 1) {
        ctx.beginPath(); ctx.arc(point.x, point.y, 3, 0, Math.PI * 2); ctx.fillStyle = "#fff"; ctx.fill();
        ctx.strokeStyle = "#1d63ed"; ctx.stroke();
      }
    });
    ctx.fillStyle = "#72839a"; ctx.font = "10px sans-serif";
    [0, 6, 12, 18, 23].forEach(hour => {
      if (hour < points.length) ctx.fillText(`${String(hour).padStart(2, "0")}:00`, points[hour].x - 12, height - 7);
    });
  }

  document.querySelectorAll("canvas[data-values]").forEach(drawChart);
  window.addEventListener("resize", () => document.querySelectorAll("canvas[data-values]").forEach(drawChart));
})();

