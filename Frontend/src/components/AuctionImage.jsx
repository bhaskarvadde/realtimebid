import { useState } from "react";

export default function AuctionImage({ src, title }) {
  const [failed, setFailed] = useState(false);

  if (!src || failed) {
    return <div className="media-fallback">{(title || "?").charAt(0).toUpperCase()}</div>;
  }
  return <img src={src} alt={title} loading="lazy" onError={() => setFailed(true)} />;
}