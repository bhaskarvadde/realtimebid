import { useEffect, useState } from "react";
import { apiFetch } from "../api/client";
import AuctionCard from "../components/AuctionCard";

const FILTERS = [
  ["ALL", "All"],
  ["ACTIVE", "Live"],
  ["SCHEDULED", "Scheduled"],
  ["ENDED", "Ended"],
];

export default function AuctionList() {
  const [auctions, setAuctions] = useState([]);
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(true);
  const [filter, setFilter] = useState("ALL");
  const [query, setQuery] = useState("");

  useEffect(() => {
    apiFetch("/auctions/all")
      .then(setAuctions)
      .catch((err) => setError(err.message))
      .finally(() => setLoading(false));
  }, []);

  const count = (key) => (key === "ALL" ? auctions.length : auctions.filter((a) => a.status === key).length);

  const visible = auctions.filter(
    (a) =>
      (filter === "ALL" || a.status === filter) &&
      (a.title || "").toLowerCase().includes(query.trim().toLowerCase())
  );

  return (
    <div className="container page">
      <div className="section-head">
        <div>
          <h1 className="section-title">Explore auctions</h1>
          <p className="section-sub">Live, upcoming and completed auctions in one place.</p>
        </div>
      </div>

      <div className="toolbar">
        <div className="chips">
          {FILTERS.map(([key, label]) => (
            <button
              key={key}
              className={`chip ${filter === key ? "active" : ""}`}
              onClick={() => setFilter(key)}
            >
              {label} ({count(key)})
            </button>
          ))}
        </div>
        <input
          className="input search"
          placeholder="Search by title…"
          value={query}
          onChange={(e) => setQuery(e.target.value)}
        />
      </div>

      {error && <div className="alert alert-error">{error}</div>}
      {loading && <p className="loading">Loading auctions…</p>}
      {!loading && !error && visible.length === 0 && (
        <div className="empty">No auctions match your filters.</div>
      )}

      <div className="grid">
        {visible.map((a) => <AuctionCard key={a.id} auction={a} />)}
      </div>
    </div>
  );
}