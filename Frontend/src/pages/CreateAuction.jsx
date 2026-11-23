import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { apiFetch } from "../api/client";
import AuctionImage from "../components/AuctionImage";
import { money } from "../utils/format";

export default function CreateAuction() {
  const [form, setForm] = useState({
    title: "",
    description: "",
    imageUrl: "",
    startingPrice: "",
    reservePrice: "",
    startTime: "",
    endTime: "",
  });
  const [error, setError] = useState("");
  const [submitting, setSubmitting] = useState(false);
  const navigate = useNavigate();

  const update = (field) => (e) => setForm({ ...form, [field]: e.target.value });

  async function handleSubmit(e) {
    e.preventDefault();
    setError("");
    setSubmitting(true);
    try {
      const created = await apiFetch("/auctions", {
        method: "POST",
        body: JSON.stringify({
          title: form.title,
          description: form.description,
          imageUrl: form.imageUrl.trim() || null,
          startingPrice: Number(form.startingPrice),
          reservePrice: form.reservePrice ? Number(form.reservePrice) : null,
          startTime: form.startTime,
          endTime: form.endTime,
        }),
      });
      navigate(`/auctions/${created.id}`);
    } catch (err) {
      setError(err.message);
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <div className="container page">
      <div className="section-head">
        <div>
          <h1 className="section-title">Create auction</h1>
          <p className="section-sub">List an item and let the bids roll in.</p>
        </div>
      </div>

      <div className="create-grid">
        <form className="panel" onSubmit={handleSubmit} noValidate>
          {error && <div className="alert alert-error">{error}</div>}

          <div className="field">
            <label htmlFor="title">Auction title</label>
            <input id="title" className="input" placeholder="e.g. Vintage film camera"
                   value={form.title} onChange={update("title")} />
          </div>

          <div className="field">
            <label htmlFor="description">Description</label>
            <textarea id="description" className="textarea" maxLength={255}
                      placeholder="Describe the item (max 255 characters)"
                      value={form.description} onChange={update("description")} />
          </div>

          <div className="field">
            <label htmlFor="imageUrl">Product image URL</label>
            <input id="imageUrl" className="input" placeholder="https://…"
                   value={form.imageUrl} onChange={update("imageUrl")} />
            <span className="hint">Paste a link to an image. The preview updates as you type.</span>
          </div>

          <div className="two">
            <div className="field">
              <label htmlFor="startingPrice">Starting price</label>
              <input id="startingPrice" className="input" type="number" step="0.01" placeholder="0.00"
                     value={form.startingPrice} onChange={update("startingPrice")} />
            </div>
            <div className="field">
              <label htmlFor="reservePrice">Reserve price (optional)</label>
              <input id="reservePrice" className="input" type="number" step="0.01" placeholder="0.00"
                     value={form.reservePrice} onChange={update("reservePrice")} />
            </div>
          </div>

          <div className="two">
            <div className="field">
              <label htmlFor="startTime">Start date and time</label>
              <input id="startTime" className="input" type="datetime-local"
                     value={form.startTime} onChange={update("startTime")} />
              <span className="hint">Set in the past to go live within 30 seconds.</span>
            </div>
            <div className="field">
              <label htmlFor="endTime">End date and time</label>
              <input id="endTime" className="input" type="datetime-local"
                     value={form.endTime} onChange={update("endTime")} />
              <span className="hint">Must be in the future.</span>
            </div>
          </div>

          <button className="btn btn-primary btn-block" type="submit" disabled={submitting}>
            {submitting ? "Creating…" : "Create auction"}
          </button>
        </form>

        <div>
          <div className="metric-label" style={{ marginBottom: 10 }}>Live preview</div>
          <div className="auction-card live preview-card">
            <div className="card-media">
              <AuctionImage key={form.imageUrl} src={form.imageUrl} title={form.title || "Preview"} />
            </div>
            <div className="card-body">
              <h3 className="card-title">{form.title || "Your auction title"}</h3>
              <div>
                <div className="metric-label">Starting price</div>
                <div className="metric-value cyan">
                  {form.startingPrice ? money(form.startingPrice) : "$0.00"}
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}