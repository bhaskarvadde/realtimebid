import { useCallback, useEffect, useState } from "react";
import { Link, useParams } from "react-router-dom";
import { Client } from "@stomp/stompjs";
import { apiFetch } from "../api/client";
import AuctionImage from "../components/AuctionImage";
import StatusBadge from "../components/StatusBadge";
import Countdown from "../components/Countdown";
import { formatDateTime, initials, money } from "../utils/format";

const QUICK_ADDS = [10, 50, 100];

export default function AuctionDetail() {
  const { id } = useParams();
  const [auction, setAuction] = useState(null);
  const [bids, setBids] = useState([]);
  const [amount, setAmount] = useState("");
  const [error, setError] = useState("");
  const [liveMessage, setLiveMessage] = useState("");
  const [submitting, setSubmitting] = useState(false);

  const loadData = useCallback(async () => {
    try {
      const a = await apiFetch(`/auctions/${id}`);
      const b = await apiFetch(`/auctions/${id}/bids`);
      setAuction(a);
      setBids(b);
    } catch (err) {
      setError(err.message);
    }
  }, [id]);

  useEffect(() => {
    loadData();

    const client = new Client({
      brokerURL: import.meta.env.VITE_WS_URL || "ws://localhost:8080/ws",
      reconnectDelay: 5000,
      onConnect: () => {
        client.subscribe(`/topic/auctions/${id}`, (message) => {
          const event = JSON.parse(message.body);

          if ("finalPrice" in event) {
            setLiveMessage(
              `Auction closed. Winner: ${event.winningBidderName ?? "no bids"} at $${event.finalPrice}`,
            );
          } else {
            setLiveMessage(`New bid: $${event.amount} by ${event.bidderName}`);
          }

          loadData();
        });
      },
    });

    client.activate();

    return () => {
      client.deactivate();
    };
  }, [id, loadData]);

  async function handleBid(e) {
    e.preventDefault();
    setError("");
    setSubmitting(true);

    try {
      await apiFetch("/bids", {
        method: "POST",
        body: JSON.stringify({
          auctionId: Number(id),
          amount: Number(amount),
        }),
      });

      setAmount("");
      loadData();
    } catch (err) {
      setError(err.message);
    } finally {
      setSubmitting(false);
    }
  }

  if (!auction) {
    return (
      <div className="container page">
        {error ? (
          <div className="alert alert-error">{error}</div>
        ) : (
          <p className="loading">Loading auction…</p>
        )}
      </div>
    );
  }

  const hasBids =
    auction.currentHighestBid !== null &&
    auction.currentHighestBid !== undefined;

  const floor = Number(
    hasBids ? auction.currentHighestBid : auction.startingPrice,
  );

  const ended = auction.status === "ENDED";

  return (
    <div className="container page">
      <Link to="/auctions" className="back-link">
        ← Back to auctions
      </Link>

      {liveMessage && <div className="alert alert-live">{liveMessage}</div>}

      <div className="detail">
        <div>
          <div className="detail-media">
            <AuctionImage src={auction.imageUrl} title={auction.title} />
          </div>

          <div className="panel" style={{ marginTop: 24 }}>
            <div className="detail-head">
              <h1 className="detail-title">{auction.title}</h1>
              <StatusBadge status={auction.status} />
            </div>

            <p className="muted">
              {auction.description || "No description provided."}
            </p>

            <div className="kv-grid">
              <div>
                <div className="metric-label">Seller</div>
                <div className="metric-value">{auction.sellerName}</div>
              </div>

              <div>
                <div className="metric-label">Starting price</div>
                <div className="metric-value">
                  {money(auction.startingPrice)}
                </div>
              </div>

              <div>
                <div className="metric-label">Total bids</div>
                <div className="metric-value">
                  {auction.bidCount ?? bids.length}
                </div>
              </div>

              <div>
                <div className="metric-label">Ends</div>
                <div className="metric-value">
                  {formatDateTime(auction.endTime)}
                </div>
              </div>
            </div>
          </div>

          <div className="panel" style={{ marginTop: 24 }}>
            <h3>Bid history</h3>

            <ul className="bid-list">
              {bids.length === 0 && (
                <li className="muted small">No bids yet.</li>
              )}

              {bids.map((b, index) => (
                <li
                  className={`bid-item ${index === 0 ? "top" : ""}`}
                  key={b.id}
                >
                  <div className="bidder">
                    <span className="avatar">{initials(b.bidderName)}</span>

                    <div>
                      <div style={{ fontWeight: 600 }}>{b.bidderName}</div>

                      {index === 0 && (
                        <div className="small" style={{ color: "var(--cyan)" }}>
                          Highest bidder
                        </div>
                      )}
                    </div>
                  </div>

                  <div>
                    <div className="bid-amount">{money(b.amount)}</div>

                    <div className="bid-time">
                      {new Date(b.createdAt).toLocaleTimeString()}
                    </div>
                  </div>
                </li>
              ))}
            </ul>
          </div>
        </div>

        <aside className="panel glass bid-panel">
          <div className="metric-label">
            {hasBids ? "Current highest bid" : "Starting price"}
          </div>

          <div className="price-hero">
            {money(hasBids ? auction.currentHighestBid : auction.startingPrice)}
          </div>

          <p className="muted small">
            {hasBids
              ? `Leading: ${bids[0]?.bidderName ?? "—"}`
              : "No bids yet. Be the first."}
          </p>

          <div className="divider" />

          <Countdown
            status={auction.status}
            startTime={auction.startTime}
            endTime={auction.endTime}
            variant="big"
          />

          <div className="divider" />

          {ended ? (
            <div className="winner-box">
              <div className="metric-label">Result</div>

              <div className="winner-name">
                {auction.winnerName
                  ? `Won by ${auction.winnerName}`
                  : "Ended with no bids"}
              </div>
            </div>
          ) : auction.status === "SCHEDULED" ? (
            <div className="winner-box">
              <div className="metric-label">Not open yet</div>

              <div className="winner-name">
                Bidding opens when this auction goes live.
              </div>
            </div>
          ) : (
            <form onSubmit={handleBid}>
              <label className="metric-label" htmlFor="bid">
                Your bid
              </label>

              <div className="bid-form">
                <div className="bid-input-wrap">
                  <span className="currency">$</span>

                  <input
                    id="bid"
                    className="input bid-input"
                    type="number"
                    step="0.01"
                    placeholder={(floor + 1).toFixed(2)}
                    value={amount}
                    onChange={(e) => setAmount(e.target.value)}
                  />
                </div>

                <button
                  className="btn btn-primary"
                  type="submit"
                  disabled={submitting}
                >
                  Place Bid
                </button>
              </div>

              <div className="quick-row">
                {QUICK_ADDS.map((n) => (
                  <button
                    type="button"
                    className="chip"
                    key={n}
                    onClick={() => setAmount((floor + n).toFixed(2))}
                  >
                    +${n}
                  </button>
                ))}
              </div>

              <p className="muted small">
                Your bid must be higher than {money(floor)}.
              </p>
            </form>
          )}

          {error && (
            <div
              className="alert alert-error"
              style={{ marginTop: 16, marginBottom: 0 }}
            >
              {error}
            </div>
          )}
        </aside>
      </div>
    </div>
  );
}
