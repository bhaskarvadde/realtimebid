import { useEffect, useMemo, useState } from "react";
import { Link, useSearchParams } from "react-router-dom";
import { apiFetch } from "../api/client";
import AuctionImage from "../components/AuctionImage";
import StatusBadge from "../components/StatusBadge";
import { formatDate, formatDateTime, initials, money } from "../utils/format";

const TABS = [
  ["bids", "My Bids"],
  ["auctions", "My Auctions"],
  ["won", "Won Auctions"],
];

function BidRow({ bid }) {
  const leading = Number(bid.amount) >= Number(bid.currentHighestBid);
  return (
    <Link to={`/auctions/${bid.auctionId}`} className="row-card">
      <div className="row-thumb">
        <AuctionImage src={bid.auctionImageUrl} title={bid.auctionTitle} />
      </div>
      <div className="row-main">
        <div className="row-title">{bid.auctionTitle}</div>
        <div className="muted small">
          Your bid <span className="num">{money(bid.amount)}</span> · Highest{" "}
          <span className="num">{money(bid.currentHighestBid)}</span>
        </div>
      </div>
      <span className={`tag ${leading ? "tag-lead" : "tag-out"}`}>{leading ? "Leading" : "Outbid"}</span>
    </Link>
  );
}

function AuctionRow({ auction, won }) {
  return (
    <Link to={`/auctions/${auction.id}`} className="row-card">
      <div className="row-thumb">
        <AuctionImage src={auction.imageUrl} title={auction.title} />
      </div>
      <div className="row-main">
        <div className="row-title">{auction.title}</div>
        <div className="muted small">
          {won ? "Won at" : "Current bid"}{" "}
          <span className="num">
            {auction.currentHighestBid ? money(auction.currentHighestBid) : money(auction.startingPrice)}
          </span>{" "}
          · {auction.bidCount} bids
        </div>
      </div>
      <StatusBadge status={auction.status} />
    </Link>
  );
}

export default function Dashboard() {
  const [params, setParams] = useSearchParams();
  const requested = params.get("tab");
  const tab = TABS.some(([key]) => key === requested) ? requested : "bids";

  const [data, setData] = useState({ user: null, bids: [], auctions: [], won: [] });
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    Promise.all([
      apiFetch("/users/me"),
      apiFetch("/bids/mine"),
      apiFetch("/auctions/mine"),
      apiFetch("/auctions/won"),
    ])
      .then(([user, bids, auctions, won]) => setData({ user, bids, auctions, won }))
      .catch((err) => setError(err.message))
      .finally(() => setLoading(false));
  }, []);

  const activeBids = useMemo(() => {
    const byAuction = new Map();
    data.bids.forEach((b) => {
      if (b.auctionStatus === "ENDED") return;
      const existing = byAuction.get(b.auctionId);
      if (!existing || Number(b.amount) > Number(existing.amount)) byAuction.set(b.auctionId, b);
    });
    return Array.from(byAuction.values());
  }, [data.bids]);

  if (loading) {
    return <div className="container page"><p className="loading">Loading your dashboard…</p></div>;
  }
  if (error) {
    return <div className="container page"><div className="alert alert-error">{error}</div></div>;
  }

  const { user } = data;

  return (
    <div className="container page">
      <div className="section-head">
        <div>
          <h1 className="section-title">Dashboard</h1>
          <p className="section-sub">Your bids, auctions and activity in one place.</p>
        </div>
        <Link to="/create" className="btn btn-primary btn-sm">+ Create Auction</Link>
      </div>

      <div className="dash-grid">
        <aside className="stack">
          <div className="panel profile-card">
            <span className="avatar profile-avatar">{initials(user?.name)}</span>
            <h3>{user?.name}</h3>
            <p className="muted small" style={{ marginBottom: 14 }}>{user?.email}</p>
            <div className="kv"><span>Role</span><span>{user?.role}</span></div>
            <div className="kv"><span>Member since</span><span>{formatDate(user?.createdAt)}</span></div>
          </div>

          <div className="panel">
            <h3>Auction activity</h3>
            <ul className="feed">
              {data.bids.length === 0 && <li className="muted small">No activity yet.</li>}
              {data.bids.slice(0, 6).map((b) => (
                <li className="feed-item" key={b.bidId}>
                  You bid <span className="num">{money(b.amount)}</span> on {b.auctionTitle}
                  <div className="muted small">{formatDateTime(b.createdAt)}</div>
                </li>
              ))}
            </ul>
          </div>
        </aside>

        <section>
          <div className="stats three">
            <div className="stat">
              <div className="stat-value cyan">{activeBids.length}</div>
              <div className="stat-label">Active bids</div>
            </div>
            <div className="stat">
              <div className="stat-value">{data.auctions.length}</div>
              <div className="stat-label">My auctions</div>
            </div>
            <div className="stat">
              <div className="stat-value">{data.won.length}</div>
              <div className="stat-label">Won auctions</div>
            </div>
          </div>

          <div className="tabs">
            {TABS.map(([key, label]) => (
              <button
                key={key}
                className={`tab ${tab === key ? "active" : ""}`}
                onClick={() => setParams({ tab: key })}
              >
                {label}
              </button>
            ))}
          </div>

          <div className="stack">
            {tab === "bids" &&
              (activeBids.length > 0 ? (
                activeBids.map((b) => <BidRow key={b.bidId} bid={b} />)
              ) : (
                <div className="empty">
                  You have no active bids. <Link to="/auctions" style={{ color: "var(--cyan)" }}>Explore auctions</Link>
                </div>
              ))}

            {tab === "auctions" &&
              (data.auctions.length > 0 ? (
                data.auctions.map((a) => <AuctionRow key={a.id} auction={a} />)
              ) : (
                <div className="empty">
                  You have not listed any auctions. <Link to="/create" style={{ color: "var(--cyan)" }}>Create one</Link>
                </div>
              ))}

            {tab === "won" &&
              (data.won.length > 0 ? (
                data.won.map((a) => <AuctionRow key={a.id} auction={a} won />)
              ) : (
                <div className="empty">No won auctions yet. Place a winning bid to see it here.</div>
              ))}
          </div>
        </section>
      </div>
    </div>
  );
}